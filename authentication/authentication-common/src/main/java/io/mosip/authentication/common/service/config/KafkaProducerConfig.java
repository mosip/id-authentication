package io.mosip.authentication.common.service.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.AUTHENTICATION_ERROR_EVENTING_ENABLED;

/**
 * The Class KafkaProducerConfig.
 * 
 * @author Neha
 */

@Configuration
@ConditionalOnProperty(value = AUTHENTICATION_ERROR_EVENTING_ENABLED, havingValue = "true", matchIfMissing = false)
public class KafkaProducerConfig {
	private static final Logger logger = IdaLogger.getLogger(KafkaProducerConfig.class);

	@Value(value = "${mosip.ida.kafka.bootstrap.servers}")
	private String bootstrapAddress;

	@Bean
	public ProducerFactory<String, Object> producerFactory() {
		logger.info("Kafka bootstrap address-----" + bootstrapAddress);
		Map<String, Object> configProps = new HashMap<>();
		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		return new DefaultKafkaProducerFactory<>(configProps);
	}

	@Bean
	public KafkaTemplate<String, Object> kafkaTemplate() {
		return new KafkaTemplate<>(producerFactory());
	}
}