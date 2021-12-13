package com.siemens.datalayer.iot.util;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class KafkaUtil {
    public static KafkaProducer<String,String> createProducer(String servers)
    {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", servers); // kafka服务器ip
        properties.put("acks", "all"); // 所有followers都响应了才认为消息提交成功，即"committed"
        properties.put("retries", 0); // retries = MAX 无限重试，直到你意识到出现了问题
        properties.put("batch.size", 16384); // producer将试图批处理消息记录，以减少请求次数.默认的批量处理消息字节数
        properties.put("key.serializer", StringSerializer.class.getName());
        properties.put("value.serializer", StringSerializer.class.getName());

        return new KafkaProducer<>(properties);
    }

    public static void send(KafkaProducer<String,String> producer, String topic,String message)
    {
        producer.send(new ProducerRecord<String,String>(topic,message));
    }
}
