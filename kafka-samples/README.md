#### kafka 连接原理

首先连接 192.168.0.141:9092
再连接返回的host.name = bogon,
最后继续连接advertised.host.name=bogon



### docker部署单机kafka

```bash
docker run -d --name zookeeper -p 2181:2181  wurstmeister/zookeeper

docker run -d --name kafka -p 9092:9092 -e KAFKA_BROKER_ID=0 -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 --link zookeeper -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://192.168.112.78:9092 -e KAFKA_LISTENERS=PLAINTEXT://0.0.0.0:9092 -t wurstmeister/kafka
```

