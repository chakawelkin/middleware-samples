package com.chaka.middleware.kafka;

import org.apache.kafka.clients.producer.*;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class KafkaProducerAnalysis {

    //配置集群地址，以逗号(,)隔开
    public static final String brokerList = "114.67.170.152:9092";
    public static final String topic = "topic-demo";

    public static Properties initConfig(){
        Properties props = new Properties();
        props.put("bootstrap.servers", brokerList);
        props.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        props.put("client.id", "producer.client.id.demo");
        props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, DemoPartitioner.class.getName());
        props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, ProducerInterceptorPrefix.class.getName());
        return props;
    }

    public static void main(String[] args) throws InterruptedException {
        Properties properties = initConfig();
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        doSync(producer);

        producer.close();

    }

    private static void doSync(KafkaProducer<String, String> producer){
        int i = 0;
        while (i < 100){
            ProducerRecord<String,String> record = new ProducerRecord<>(topic, "hello kafka!" + i++);
            producer.send(record);
        }
    }

    private static void doAsync(KafkaProducer<String, String> producer){
        ProducerRecord<String,String> record = new ProducerRecord<>(topic,"hello kafka!");

        try {
            System.out.println("当前线程:" + Thread.currentThread().getName());
            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    exception.printStackTrace();
                }else {
                    //callback方式的执行是异步的
                    System.out.println("执行线程:" + Thread.currentThread().getName());
                    System.out.println(metadata.topic() + "-" +
                            metadata.partition() + ":" + metadata.offset());
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}