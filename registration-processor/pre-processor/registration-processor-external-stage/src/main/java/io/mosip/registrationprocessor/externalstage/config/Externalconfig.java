package io.mosip.registrationprocessor.externalstage.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import io.mosip.registrationprocessor.externalstage.stage.ExternalStage;

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
