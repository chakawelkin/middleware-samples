package com.chaka.middleware.spring.conf;

import com.chaka.middleware.spring.message.Demo01Message;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig{

    @Bean
    public NewTopic topic1() {
        return TopicBuilder.name(Demo01Message.TOPIC)
                .partitions(3)
                .replicas(3)
                .compact()
                .build();
    }

}