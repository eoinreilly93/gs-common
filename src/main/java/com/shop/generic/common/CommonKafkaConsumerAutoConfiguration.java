package com.shop.generic.common;

import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
@Slf4j
public class CommonKafkaConsumerAutoConfiguration {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.properties.[spring.json.trusted.packages]}")
    private String trustedPackages;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    //Default value is 'latest' if not specified in property files
    @Value("${spring.kafka.consumer.auto-offset-reset:latest}")
    private String consumerAutoOffsetReset;

    @Bean
    public ConsumerFactory<String, ?> consumerFactory() {
        log.info("Creating generic Kafka consumer factory");
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    @Bean
    public Map<String, Object> consumerConfigs() {
        return Map.of(
                BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class,
                JsonDeserializer.TRUSTED_PACKAGES, trustedPackages,
                AUTO_OFFSET_RESET_CONFIG, consumerAutoOffsetReset,
                GROUP_ID_CONFIG, groupId
        );
    }
}
