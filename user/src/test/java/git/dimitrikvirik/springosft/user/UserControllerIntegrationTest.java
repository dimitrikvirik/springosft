package git.dimitrikvirik.springosft.user;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import com.hazelcast.core.HazelcastInstance;
import git.dimitrikvirik.springsoft.common.model.dto.UserKafkaDTO;
import git.dimitrikvirik.springsoft.common.services.JwtTokenGenerator;
import git.dimitrikvirik.springsoft.common.services.JwtTokenReader;
import git.dimitrikvirik.springsoft.user.UserApplication;
import git.dimitrikvirik.springsoft.user.model.dto.UserDTO;
import git.dimitrikvirik.springsoft.user.model.param.UserCreateParam;
import git.dimitrikvirik.springsoft.user.model.param.UserUpdateParam;
import git.dimitrikvirik.springsoft.user.repository.UserRepository;
import io.jsonwebtoken.impl.DefaultClaims;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.net.URI;
import java.time.Duration;
import java.util.*;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.mockito.ArgumentMatchers.anyString;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = UserApplication.class)
@Testcontainers
@AutoConfigureMockMvc(addFilters = false)
class UserControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Container
    static KafkaContainer kafka = new KafkaContainer("apache/kafka-native:3.8.0");
    @Container
    static GenericContainer<?> hazelcast = new GenericContainer<>(DockerImageName.parse("hazelcast/hazelcast:latest"))
            .withExposedPorts(5701)
            .withCreateContainerCmdModifier(cmd -> cmd.withHostConfig(
                    new HostConfig().withPortBindings(new PortBinding(Ports.Binding.bindPort(5701), new ExposedPort(5701)))
            ));

    @MockBean
    private JwtTokenReader jwtTokenReader;

    @MockBean
    private JwtTokenGenerator jwtTokenGenerator;

    @Autowired
    private UserRepository userRepository;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;


    @Qualifier("hazelcastInstance")
    @Autowired
    private HazelcastInstance hazelcastClient;

    private String baseUrl;


    @TestConfiguration
    static class KafkaTestConfig {
        @Bean
        public ProducerFactory<String, UserKafkaDTO> producerFactory() {
            Map<String, Object> configProps = new HashMap<>();
            configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
            configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
            return new DefaultKafkaProducerFactory<>(configProps);
        }

        @Bean
        public KafkaTemplate<String, UserKafkaDTO> kafkaTemplate() {
            return new KafkaTemplate<>(producerFactory());
        }
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("spring.hazelcast.network.addresses", () -> "localhost:" + hazelcast.getMappedPort(5701));
    }

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/users";

        Mockito.when(jwtTokenReader.extractAllClaims(anyString())).thenReturn(new DefaultClaims(Map.of(
                "id", 1,
                "authorities", List.of("GET_USERS", "DELETE_USER")
        )));

    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }
    private static final String BEARER_TOKEN = "Bearer test-token";

    @Test
    void testGetAllUsers() {
        ResponseEntity<LinkedHashMap<String, Object>> response = restTemplate.exchange(
                RequestEntity.get(URI.create(baseUrl + "?page=0&size=10&sort=id,asc"))
                        .header("Authorization", BEARER_TOKEN)
                        .build(),
                new ParameterizedTypeReference<>() {}
        );


        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertTrue(((List<?>) response.getBody().get("content")).isEmpty());
    }

    @Test
    void testGetUserById() {
        UserCreateParam newUser = new UserCreateParam("John", "Doe", "johndoe", "john@example.com", "password123");
        ResponseEntity<UserDTO> createResponse = restTemplate.exchange(
                RequestEntity.post(URI.create(baseUrl))
                        .header("Authorization", BEARER_TOKEN)
                        .body(newUser),
                UserDTO.class
        );
        Assertions.assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        Assertions.assertNotNull(createResponse.getBody());

        Long userId = createResponse.getBody().getId();

        ResponseEntity<UserDTO> getResponse = restTemplate.exchange(
                RequestEntity.get(URI.create(baseUrl + "/" + userId))
                        .header("Authorization", BEARER_TOKEN)
                        .build(),
                UserDTO.class
        );

        Assertions.assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        Assertions.assertNotNull(getResponse.getBody());
        Assertions.assertEquals(userId, getResponse.getBody().getId());
        Assertions.assertEquals(newUser.getFirstname(), getResponse.getBody().getFirstname());
        Assertions.assertEquals(newUser.getLastname(), getResponse.getBody().getLastname());
        Assertions.assertEquals(newUser.getUsername(), getResponse.getBody().getUsername());
        Assertions.assertEquals(newUser.getEmail(), getResponse.getBody().getEmail());
        Assertions.assertNotNull(getResponse.getBody().getCreatedAt());

        // Check if user is cached in Hazelcast
        UserDTO cachedUser = (UserDTO) hazelcastClient.getMap("users").get(userId);
        Assertions.assertNotNull(cachedUser);
        Assertions.assertEquals(getResponse.getBody().getId(), cachedUser.getId());
    }

    @Test
    void testCreateUser() {
        UserCreateParam newUser = new UserCreateParam("Jane", "Doe", "janedoe", "jane@example.com", "password456");
        ResponseEntity<UserDTO> response = restTemplate.exchange(
                RequestEntity.post(URI.create(baseUrl))
                        .header("Authorization", BEARER_TOKEN)
                        .body(newUser),
                UserDTO.class
        );

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(newUser.getFirstname(), response.getBody().getFirstname());
        Assertions.assertEquals(newUser.getLastname(), response.getBody().getLastname());
        Assertions.assertEquals(newUser.getUsername(), response.getBody().getUsername());
        Assertions.assertEquals(newUser.getEmail(), response.getBody().getEmail());
        Assertions.assertNotNull(response.getBody().getId());
        Assertions.assertNotNull(response.getBody().getCreatedAt());
        Assertions.assertTrue(hazelcastClient.getMap("users").isEmpty());

    }

    @Test
    void testUpdateUser() {
        UserCreateParam newUser = new UserCreateParam("Alice", "Smith", "alicesmith", "alice@example.com", "password789");
        ResponseEntity<UserDTO> createResponse = restTemplate.exchange(
                RequestEntity.post(URI.create(baseUrl))
                        .header("Authorization", BEARER_TOKEN)
                        .body(newUser),
                UserDTO.class
        );
        Assertions.assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        Assertions.assertNotNull(createResponse.getBody());

        Long userId = createResponse.getBody().getId();

        UserUpdateParam updatedUser = new UserUpdateParam("Alicia", "Johnson", "aliciaj", "alicia@example.com", "password321");

        ResponseEntity<UserDTO> updateResponse = restTemplate.exchange(
                RequestEntity.put(URI.create(baseUrl + "/" + userId))
                        .header("Authorization", BEARER_TOKEN)
                        .body(updatedUser),
                UserDTO.class
        );

        Assertions.assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        Assertions.assertNotNull(updateResponse.getBody());
        Assertions.assertEquals(updatedUser.getFirstname(), updateResponse.getBody().getFirstname());
        Assertions.assertEquals(updatedUser.getLastname(), updateResponse.getBody().getLastname());
        Assertions.assertEquals(updatedUser.getUsername(), updateResponse.getBody().getUsername());
        Assertions.assertEquals(updatedUser.getEmail(), updateResponse.getBody().getEmail());
        Assertions.assertEquals(userId, updateResponse.getBody().getId());
        Assertions.assertNotNull(updateResponse.getBody().getCreatedAt());

        // Check if user is updated in Hazelcast cache
        Assertions.assertTrue(hazelcastClient.getMap("users").isEmpty());

    }

    @Test
    void testDeleteUser() throws InterruptedException {
        UserCreateParam newUser = new UserCreateParam("Bob", "Brown", "bobbrown", "bob@example.com", "password101");
        ResponseEntity<UserDTO> createResponse = restTemplate.exchange(
                RequestEntity.post(URI.create(baseUrl))
                        .header("Authorization", BEARER_TOKEN)
                        .body(newUser),
                UserDTO.class
        );
        Assertions.assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        Assertions.assertNotNull(createResponse.getBody());

        UserDTO responseBody = createResponse.getBody();
        Long userId = responseBody.getId();

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                RequestEntity.delete(URI.create(baseUrl + "/" + userId))
                        .header("Authorization", BEARER_TOKEN)
                        .build(),
                Void.class
        );

        Assertions.assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        ResponseEntity<UserDTO> getResponse = restTemplate.exchange(
                RequestEntity.get(URI.create(baseUrl + "/" + userId))
                        .header("Authorization", BEARER_TOKEN)
                        .build(),
                UserDTO.class
        );
        Assertions.assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());

        Assertions.assertTrue(hazelcastClient.getMap("users").isEmpty());

        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(kafka.getBootstrapServers(), "test", "true");
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        KafkaConsumer<String, UserKafkaDTO> consumer = new KafkaConsumer<>(consumerProps);

        consumer.subscribe(List.of("user-topic"));
        ConsumerRecords<String, UserKafkaDTO> records = consumer.poll(Duration.of(10, SECONDS));
        ArrayList<ConsumerRecord<String, UserKafkaDTO>> recordLists = Lists.newArrayList(records.iterator());

        Assertions.assertEquals(1, recordLists.size());
        Assertions.assertFalse(recordLists.get(0).value().enabled());
        Assertions.assertEquals(userId, recordLists.get(0).value().userId());
        Assertions.assertEquals(responseBody.getUsername(), recordLists.get(0).value().username());

        consumer.close();


    }


}
