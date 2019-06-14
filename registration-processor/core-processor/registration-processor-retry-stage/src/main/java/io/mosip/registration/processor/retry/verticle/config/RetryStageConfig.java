package io.mosip.registration.processor.retry.verticle.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.registration.processor.retry.verticle.stages.RetryStage;

/**
 * The Class RetryStageConfig.
 */
@Configuration
public class RetryStageConfig {

	/**
	 * Gets the retry stage.
	 *
	 * @return the retry stage
	 */
	@Bean
	public RetryStage getRetryStage() {
		return new RetryStage();
	}

	/**
	 * Gets the mapper.
	 *
	 * @return the mapper
	 */
	@Bean
	public ObjectMapper getMapper() {
		return new ObjectMapper();
	}
}
