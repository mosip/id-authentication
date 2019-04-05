package io.mosip.registrationprocessor.externalStage.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import io.mosip.registrationprocessor.externalStage.stage.ExternalStage;

/**
 * external stage beans configuration class
 *
 */
@Configuration
@EnableAspectJAutoProxy
public class Externalconfig {
	/**
	 * ExternalStage bean
	 */
	@Bean
	public ExternalStage externalStage() {
		return new ExternalStage();
	}
}
