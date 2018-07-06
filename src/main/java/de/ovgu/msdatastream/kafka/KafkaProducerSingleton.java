package de.ovgu.msdatastream.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.HashMap;
import java.util.Map;

public class KafkaProducerSingleton {
    private static KafkaProducer<String, String> singletonInstance;

    private KafkaProducerSingleton() { }

    public static KafkaProducer<String, String> getSingletonInstance(String url){
        if (singletonInstance == null) {
            Map<String, Object> producerConfig = new HashMap<>();
            producerConfig.put("bootstrap.servers", url);
            producerConfig.put("metadata.fetch.timeout.ms", "3000");
            producerConfig.put("request.timeout.ms", "3000");
            // ... other options: http://kafka.apache.org/documentation.html#producerconfigs
            StringSerializer serializer = new StringSerializer();
            singletonInstance = new KafkaProducer<>(producerConfig, serializer, serializer);
        }

        return singletonInstance;
    }


}
