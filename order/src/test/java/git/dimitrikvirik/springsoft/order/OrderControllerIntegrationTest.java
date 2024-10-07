package git.dimitrikvirik.springsoft.order;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import com.hazelcast.core.HazelcastInstance;
import git.dimitrikvirik.springsoft.order.model.OrderStatus;
import git.dimitrikvirik.springsoft.order.model.param.OrderParam;
import git.dimitrikvirik.springsoft.order.repository.OrderRepository;
import git.dimitrikvirik.springsoft.order.service.JwtService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = OrderApplication.class)
@Testcontainers
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class OrderControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Container
    static GenericContainer<?> hazelcast = new GenericContainer<>(DockerImageName.parse("hazelcast/hazelcast:latest"))
            .withExposedPorts(5701)
            .withCreateContainerCmdModifier(cmd -> cmd.withHostConfig(
                    new HostConfig().withPortBindings(new PortBinding(Ports.Binding.bindPort(5701), new ExposedPort(5701)))
            ));

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Qualifier("hazelcastInstance")
    @Autowired
    private HazelcastInstance hazelcastClient;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private OrderRepository orderRepository;


    private String baseUrl;


    private static final PrincipalDTO principal = new PrincipalDTO(1L, List.of());


    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.hazelcast.network.addresses", () -> "localhost:" + hazelcast.getMappedPort(5701));
    }

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/orders";

        Mockito.when(jwtService.extractAllClaims(anyString())).thenReturn(Map.of(
                "id", principal.id(), "authorities", principal.authorities()
        ));
        orderRepository.deleteAll();
    }


    @Test
    void testGetAllOrders() {
        ResponseEntity<LinkedHashMap<String, Object>> response = restTemplate.exchange(
                RequestEntity.get(baseUrl + "?page=0&size=10&sort=id,asc")
                        .header("Authorization", "Bearer token")
                        .build(),
                new ParameterizedTypeReference<>() {}
        );

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertTrue(((List<?>) response.getBody().get("content")).isEmpty());
    }

    @Test
    void testGetOrderById() {
        OrderParam newOrder = new OrderParam("Test Product", 1L, 10.0, OrderStatus.CREATED);
        ResponseEntity<OrderDTO> createResponse = restTemplate.exchange(
                RequestEntity.post(baseUrl)
                        .header("Authorization", "Bearer token")
                        .body(newOrder),
                OrderDTO.class
        );
        Assertions.assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        Assertions.assertNotNull(createResponse.getBody());

        Long orderId = createResponse.getBody().getId();

        ResponseEntity<OrderDTO> getResponse = restTemplate.exchange(
                RequestEntity.get(baseUrl + "/" + orderId)
                        .header("Authorization", "Bearer token")
                        .build(),
                OrderDTO.class
        );

        Assertions.assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        Assertions.assertNotNull(getResponse.getBody());
        Assertions.assertEquals(orderId, getResponse.getBody().getId());
        Assertions.assertEquals(principal.id(), getResponse.getBody().getUserId());

        // Check if order is cached in Hazelcast
        OrderDTO cachedOrder = (OrderDTO) hazelcastClient.getMap("orders").get(orderId);
        Assertions.assertNotNull(cachedOrder);
        Assertions.assertEquals(getResponse.getBody().getId(), cachedOrder.getId());
    }

    @Test
    void testCreateOrder() {
        OrderParam newOrder = new OrderParam("Test Product", 1L, 10.0, OrderStatus.CREATED);
        ResponseEntity<OrderDTO> response = restTemplate.exchange(
                RequestEntity.post(baseUrl)
                        .header("Authorization", "Bearer token")
                        .body(newOrder),
                OrderDTO.class
        );

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getId());
        Assertions.assertEquals(principal.id(), response.getBody().getUserId());
        Assertions.assertEquals(response.getBody().getStatus(), OrderStatus.CREATED);
        Assertions.assertEquals(response.getBody().getPrice(), 10.0);
        Assertions.assertEquals(response.getBody().getQuantity(), 1L);
        Assertions.assertNotNull(response.getBody().getCreatedAt());
        Assertions.assertEquals("Test Product", response.getBody().getProduct());

        // Check if order is not cached immediately after creation
        Assertions.assertTrue(hazelcastClient.getMap("orders").isEmpty());
    }

    @Test
    void testUpdateOrder() {
        OrderParam newOrder = new OrderParam("Test Product", 1L, 10.0, OrderStatus.CREATED);
        ResponseEntity<OrderDTO> createResponse = restTemplate.exchange(
                RequestEntity.post(baseUrl)
                        .header("Authorization", "Bearer token")
                        .body(newOrder),
                OrderDTO.class
        );
        Assertions.assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        Assertions.assertNotNull(createResponse.getBody());

        Long orderId = createResponse.getBody().getId();

        OrderParam updatedOrder = new OrderParam("Updated Product", 1L, 20.0, OrderStatus.CANCELLED);

        ResponseEntity<OrderDTO> updateResponse = restTemplate.exchange(
                RequestEntity.put(baseUrl + "/" + orderId)
                        .header("Authorization", "Bearer token")
                        .body(updatedOrder),
                OrderDTO.class
        );

        Assertions.assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        Assertions.assertNotNull(updateResponse.getBody());
        Assertions.assertEquals(principal.id(), updateResponse.getBody().getUserId());
        Assertions.assertEquals(updateResponse.getBody().getStatus(), OrderStatus.CANCELLED);
        Assertions.assertEquals(updateResponse.getBody().getPrice(), 20.0);
        Assertions.assertEquals(updateResponse.getBody().getQuantity(), 1L);
        Assertions.assertNotNull(updateResponse.getBody().getCreatedAt());
        Assertions.assertEquals("Updated Product", updateResponse.getBody().getProduct());

        // Check if order is updated in Hazelcast cache
        Assertions.assertTrue(hazelcastClient.getMap("orders").isEmpty());
    }

    @Test
    void testDeleteOrder() {
        OrderParam newOrder = new OrderParam("Test Product", 1L, 10.0, OrderStatus.CREATED);


        ResponseEntity<OrderDTO> createResponse = restTemplate.exchange(
                RequestEntity.post(baseUrl)
                        .header("Authorization", "Bearer token")
                        .body(newOrder),
                OrderDTO.class
        );


        Assertions.assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        Assertions.assertNotNull(createResponse.getBody());

        Long orderId = createResponse.getBody().getId();


        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                RequestEntity.delete(baseUrl + "/" + orderId)
                        .header("Authorization", "Bearer token")
                        .build(),
                Void.class
        );

        Assertions.assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        ResponseEntity<OrderDTO> getResponse = restTemplate.exchange(
                RequestEntity.get(baseUrl + "/" + orderId)
                        .header("Authorization", "Bearer token")
                        .build(),
                OrderDTO.class
        );
        Assertions.assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
        Assertions.assertTrue(hazelcastClient.getMap("orders").isEmpty());
    }

}