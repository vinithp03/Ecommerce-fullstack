package com.vinith.catalog.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaStartupLogger implements CommandLineRunner {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            // Send a simple test message to check Kafka connection
            kafkaTemplate.send("test-topic", "Kafka connection check");
            System.out.println("✅ Kafka is connected and test message sent!");
        } catch (Exception e) {
            System.err.println("❌ Kafka connection failed: " + e.getMessage());
        }
    }
}
