package com.javatechie;
import com.javatechie.dto.Customer;
import com.javatechie.service.KafkaMessagePublisher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

@SpringBootTest
@Testcontainers
class KafkaProducerExampleApplicationTests {

    // Use official Apache Kafka image (native or standard)
    @Container
    @ServiceConnection // Replaces DynamicPropertyRegistry entirely in Spring Boot 4.x
    static KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("apache/kafka:4.1.1") // Supports KRaft natively
    );

//    @Container
//    static final KafkaContainer kafka = new KafkaContainer(
//            DockerImageName.parse("apache/kafka:4.1.1") // Latest 2026 KRaft image
//    )
//            // 1. Set the Role INSIDE the container environment
//            .withEnv("KAFKA_PROCESS_ROLES", "broker,controller")
//            .withEnv("KAFKA_NODE_ID", "1")
//            .withEnv("KAFKA_CONTROLLER_QUORUM_VOTERS", "1@localhost:9093");
//
//    @DynamicPropertySource
//    static void overrideProperties(DynamicPropertyRegistry registry) {
//        // 2. Map the container's dynamic port to Spring's bootstrap-servers property
//        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
//
//        // You can also pass the process roles to the Spring context if your
//        // code specifically reads them for custom logic:
//        registry.add("custom.kafka.roles", () -> "broker,controller");
//    }

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private KafkaMessagePublisher publisher;

    @Test
    void testProducerConsumerFlow() {
        String topic = "test-topic-2026";
        String message = "Hello from Testcontainers!";

        kafkaTemplate.send(topic, message);

        // Use Awaitility for asynchronous assertions
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            // Verify your listener processed the message (e.g., check DB or a mock)
            // Example: assertThat(service.getReceivedCount()).isEqualTo(1);
        });
    }

    @Test
    public void testSendEventsToTopic() {
        publisher.sendEventsToTopic(new Customer(263, "test user", "test@gmail.com", "564782542752"));
        await().pollInterval(Duration.ofSeconds(3))
                .atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
                    // assert statement
                });
    }
}
