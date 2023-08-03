package com.portabull.cache;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelCastConfiguration {

    @Autowired(required = false)
    HazelcastInstance hazelcastInstance;

    public HazelcastInstance getHazelcastInstance() {
        return hazelcastInstance;
    }

    @Bean
    @ConditionalOnProperty(value = "hazel.client.creation.isEnabled", havingValue = "true")
    public IMap<String, Object> createHazelCastClientMap() {
        if (hazelcastInstance != null) {
            return hazelcastInstance.getMap("portable-cache");
        }
        return null;
    }

    @Bean
    @ConditionalOnProperty(value = "hazel.client.creation.isEnabled", havingValue = "true")
    HazelcastInstance getHazelcastClientInstance() {
        return Hazelcast.newHazelcastInstance();
    }










/*
    @Bean
    @ConditionalOnProperty(value = "hazel.client.creation.isEnabled", havingValue = "true")
    HazelcastInstance getHazelcastClientInstance() {
                    ClientConfig clientConfig = new ClientConfig();
                clientConfig.getNetworkConfig()
                        .setConnectionAttemptLimit(0)
                        .getEurekaConfig()
                        .setEnabled(true)
                        .setProperty("registration.enabled", "false")
                        .setProperty("self-registration", "false")
                        .setProperty("namespace", "hazelcast")
                        .setProperty("name", "hazelcast")
                        .setProperty("use-classpath-eureka-client-props", "false")
                        .setProperty("shouldUseDns", "false")
                        .setProperty("serviceUrl.default", eurekaServiceUrl);
                NearCacheConfig nearCacheConfig = new NearCacheConfig();
                nearCacheConfig.setInMemoryFormat(InMemoryFormat.OBJECT);
                nearCacheConfig.setSerializeKeys(true);
                nearCacheConfig.setInvalidateOnChange(true);
                nearCacheConfig.getEvictionConfig().setEvictionPolicy(EvictionPolicy.NONE);
                nearCacheConfig.getEvictionConfig().setMaximumSizePolicy(EvictionConfig.MaxSizePolicy.ENTRY_COUNT);
                clientConfig.addNearCacheConfig(nearCacheConfig);
                return HazelcastClient.newHazelcastClient(clientConfig);
                Config config = new Config();
                config.getNetworkConfig().setPort(hazelCastPort);
                config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
                config.getNetworkConfig().getJoin().getEurekaConfig()
                        .setEnabled(true)
                        .setProperty("self-registration","true")
                        .setProperty("namespace","hazelcast")
                        .setProperty("name","hazelcast")
                        .setProperty("use-classpath-eureka-client-props","false")
                        .setProperty("shouldUseDns","false")
                        .setProperty("serviceUrl.default",eurekaServiceUrl);

                config.setInstanceName("my-cache");
                Set<HazelcastInstance> allHazelcastInstances = Hazelcast.getAllHazelcastInstances();

                HazelcastInstance HazelcastInstance = Hazelcast.getOrCreateHazelcastInstance(config);

                return Hazelcast.newHazelcastInstance(config);
    }

 */

}
