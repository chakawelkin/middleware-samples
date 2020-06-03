package com.chaka.middleware.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class KafkaConsumerAnalysis {

    /**
     * 指定连接的broker地址
     */
    public static final String brokerList = "114.67.170.152:9092";
    public static final String topic = "topic-demo";
    /**
     * 消费者隶属的消费组名称，默认为""，null会异常
     */
    public static final String groupId = "group.demo";
    public static final AtomicBoolean isRunning = new AtomicBoolean(true);

    public static Properties initConfig(){
        Properties props = new Properties();
        props.put("key.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("bootstrap.servers", brokerList);
        props.put("group.id", groupId);
        props.put("client.id", "consumer.client.id.demo");
        return props;
    }

    public static void main(String[] args) {
        Properties props = initConfig();
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        msgAssign(consumer);
    }

    /**
     * 直接指定订阅某个主题的某些分区
     */
    private static void msgAssign(KafkaConsumer<String, String> consumer){
        TopicPartition tp = new TopicPartition(topic, 3);
        consumer.assign(Arrays.asList(tp));

        long lastConsumedOffset = -1;//当前消费到的位移
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(1000);
            if (records.isEmpty()) {
                break;
            }
            List<ConsumerRecord<String, String>> partitionRecords
                    = records.records(tp);
            lastConsumedOffset = partitionRecords
                    .get(partitionRecords.size() - 1).offset();
            consumer.commitSync();//同步提交消费位移
        }
        System.out.println("comsumed offset is " + lastConsumedOffset);
        OffsetAndMetadata offsetAndMetadata = consumer.committed(tp);
        System.out.println("commited offset is " + offsetAndMetadata.offset());
        long posititon = consumer.position(tp);
        System.out.println("the offset of the next record is " + posititon);
    }

    /**
     * 消息订阅式
     * @param consumer
     */
    private static void msgSubscribe(KafkaConsumer<String, String> consumer){
        consumer.subscribe(Arrays.asList(topic));
        //重载方法的ConsumerRebalanceListener -- 用来设置再均衡监听器的

        //consumer.assign(); 该方法可以用来指定订阅主题的特定分区
        try {
            while (isRunning.get()) {
                ConsumerRecords<String, String> records =
                        consumer.poll(Duration.ofMillis(1000));

                //1.消费所有的partition
                for (ConsumerRecord<String, String> record : records) {
                    System.out.println("topic = " + record.topic()
                            + ", partition = "+ record.partition()
                            + ", offset = " + record.offset());
                    System.out.println("key = " + record.key()
                            + ", value = " + record.value());
                    //do something to process record.
                }

                //2.消费指定的partition
                //records.records(new TopicPartition("",1));
            }
        } catch (Exception e) {
            log.error("occur exception ", e);
        } finally {
            consumer.close();
        }
    }

}