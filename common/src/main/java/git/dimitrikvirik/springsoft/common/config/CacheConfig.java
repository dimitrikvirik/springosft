package git.dimitrikvirik.springsoft.common.config;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.SerializationConfig;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import git.dimitrikvirik.springsoft.common.services.CacheFactory;
import git.dimitrikvirik.springsoft.common.utils.PageSerializer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class CacheConfig {

    @Value("${hazelcast.host}")
    private String hazelcastHost;

    private final List<CacheFactory> cacheFactories;

    @Bean
    public ClientConfig hazelcastClientConfig() {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.getNetworkConfig().addAddress(hazelcastHost);
        clientConfig.getTpcConfig().setEnabled(true);
        SerializationConfig serializationConfig = clientConfig.getSerializationConfig();
        cacheFactories.forEach(cacheFactory -> serializationConfig.addDataSerializableFactory(cacheFactory.getFactoryId(), cacheFactory.newInstance()));

        SerializerConfig pageSerializerConfig = new SerializerConfig()
                .setImplementation(new PageSerializer())
                .setTypeClass(Page.class);

        serializationConfig.addSerializerConfig(pageSerializerConfig);

        return clientConfig;
    }

    @Bean
    public HazelcastInstance hazelcastInstance(ClientConfig clientConfig) {
        return HazelcastClient.newHazelcastClient(clientConfig);
    }

    @Bean
    public CacheManager cacheManager(@Qualifier("hazelcastInstance") HazelcastInstance hazelcastInstance) {
        return new HazelcastCacheManager(hazelcastInstance);
    }

}
