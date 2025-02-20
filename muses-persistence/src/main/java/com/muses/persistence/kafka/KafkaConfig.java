package com.muses.persistence.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.ssl.DefaultSslBundleRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import java.util.Map;

/**
 * @ClassName KafkaConfig
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/12/11 10:11
 */
@Configuration()
public class KafkaConfig {
    //kafka配置bean注入
    @Primary
    @ConfigurationProperties(prefix = "spring.kafka.datasource.pub-video")
    @Bean(name = "pubVideoKafkaProperties")
    public KafkaProperties pubVideoKafkaProperties() {
        return new KafkaProperties();
    }

    @ConfigurationProperties(prefix = "spring.kafka.datasource.transcode-video")
    @Bean(name = "transcodeVideoKafkaProperties")
    public KafkaProperties transcodeVideoKafkaProperties() {
        return new KafkaProperties();
    }


    //kafka生产者配置
//    @Primary
//    @Bean(name = "kafkaTemplate", destroyMethod = "destroy")
//    public KafkaTemplate kafkaTemplate(@Autowired @Qualifier("pubVideoKafkaProperties") KafkaProperties kafkaProperties) {
//        return new KafkaTemplate(this.getProducerFactory(kafkaProperties));
//    }
//    @Primary
    @Bean(name = "pubVideoKafkaTemplate", destroyMethod = "destroy")
    public KafkaTemplate pubVideoKafkaTemplate(@Autowired @Qualifier("pubVideoKafkaProperties") KafkaProperties kafkaProperties) {
        return new KafkaTemplate(this.getProducerFactory(kafkaProperties));
    }


    private ProducerFactory<String, String> getProducerFactory(KafkaProperties kafkaProperties) {
        return new DefaultKafkaProducerFactory<>(this.getProducerConfigs(kafkaProperties));
    }

    private Map<String, Object> getProducerConfigs(KafkaProperties kafkaProperties) {
        return kafkaProperties.buildProducerProperties(null);
    }


    //kafka消费者配置
    @Primary
    @Bean(name = "pubVideoConsumerFactory")
    public ConsumerFactory<Object, Object> pubVideoConsumerFactory(@Autowired @Qualifier("pubVideoKafkaProperties") KafkaProperties kafkaProperties) {
        return new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties(new DefaultSslBundleRegistry()));
    }

    @Bean(name = "pubVideoKafkaListenerContainerFactory")
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Object, Object>> pubVideoKafkaListenerContainerFactory(
            @Autowired @Qualifier("pubVideoKafkaProperties") KafkaProperties kafkaProperties,
            @Autowired @Qualifier("pubVideoConsumerFactory") ConsumerFactory<Object, Object> consumerFactory
    ) {
        return getKafkaListenerContainerFactory(kafkaProperties, consumerFactory);
    }

    @Bean(name = "transcodeVideoConsumerFactory")
    public ConsumerFactory<Object, Object> transcodeVideoConsumerFactory(@Autowired @Qualifier("transcodeVideoKafkaProperties") KafkaProperties kafkaProperties) {
        return new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties(new DefaultSslBundleRegistry()));
    }

    @Bean(name = "transcodeVideoKafkaListenerContainerFactory")
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Object, Object>> transcodeVideoKafkaListenerContainerFactory(
            @Autowired @Qualifier("transcodeVideoKafkaProperties") KafkaProperties kafkaProperties,
            @Autowired @Qualifier("transcodeVideoConsumerFactory") ConsumerFactory<Object, Object> consumerFactory
    ) {
        return getKafkaListenerContainerFactory(kafkaProperties, consumerFactory);
    }

    private KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Object, Object>> getKafkaListenerContainerFactory(KafkaProperties kafkaProperties, ConsumerFactory<Object, Object> consumerFactory) {
        // 创建支持并发消费的容器
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        if (kafkaProperties.getListener().getConcurrency() != null) {
            // 设置容器的并发级别
            factory.setConcurrency(kafkaProperties.getListener().getConcurrency());
        }
        if (kafkaProperties.getListener().getAckMode() != null) {
            // 设置Kafka消息监听器容器的确认模式
            factory.getContainerProperties().setAckMode(kafkaProperties.getListener().getAckMode());
        }
        return factory;
    }

}
