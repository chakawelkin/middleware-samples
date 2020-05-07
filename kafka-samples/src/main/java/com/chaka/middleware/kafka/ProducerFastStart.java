package com.chaka.middleware.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class ProducerFastStart {

    public static final String brokerList = "114.67.170.152:9092";
    public static final String topic = "topic-demo";

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("bootstrap.servers", brokerList);


        KafkaProducer<String, String> producer =
                new KafkaProducer<>(properties);
        ProducerRecord<String, String> record =
                new ProducerRecord<>(topic, "hello, Kafka!");
        try {
            producer.send(record);
        } catch (Exception e) {
            e.printStackTrace();
        }
        producer.close();
    }

}