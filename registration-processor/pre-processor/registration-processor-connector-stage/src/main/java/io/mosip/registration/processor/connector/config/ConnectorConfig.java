package io.mosip.registration.processor.connector.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import io.mosip.registration.processor.connector.stage.ConnectorStage;

/**
 * Bean configuration class for connector stage
 * @author Jyoti Prakash Nayak
 *
 */
@Configuration
@EnableAspectJAutoProxy
public class ConnectorConfig {
	/**
	 * ConnectorStage bean
	 * @return ConnectorStage object
	 */
	@Bean
	public ConnectorStage connectorStage() {
		return new ConnectorStage();
	}

}
