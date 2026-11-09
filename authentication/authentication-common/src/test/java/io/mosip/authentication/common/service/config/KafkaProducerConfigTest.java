package io.mosip.authentication.common.service.config;

import static org.junit.Assert.*;

import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@RunWith(MockitoJUnitRunner.class)
public class KafkaProducerConfigTest {

    private KafkaProducerConfig config;

    @Before
    public void setup() throws Exception {
        config = new KafkaProducerConfig();

        // 🔑 Inject @Value field using reflection
        Field field = KafkaProducerConfig.class
                .getDeclaredField("bootstrapAddress");
        field.setAccessible(true);
        field.set(config, "localhost:9092");
    }

    /* ---------------- producerFactory ---------------- */

    @Test
    public void testProducerFactory() {
        ProducerFactory<String, Object> factory = config.producerFactory();

        assertNotNull(factory);
    }

    /* ---------------- kafkaTemplate ---------------- */

    @Test
    public void testKafkaTemplate() {
        KafkaTemplate<String, Object> template = config.kafkaTemplate();

        assertNotNull(template);
    }
}
