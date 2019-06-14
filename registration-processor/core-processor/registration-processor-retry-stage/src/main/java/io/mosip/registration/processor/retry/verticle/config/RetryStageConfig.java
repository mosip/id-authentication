package io.mosip.registration.processor.retry.verticle.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.registration.processor.retry.verticle.stages.RetryStage;

@Configuration
public class RetryStageConfig {

	@Bean
	public RetryStage getRetryStage() {
		return new RetryStage();
	}

	@Bean
	public ObjectMapper getMapper() {
		return new ObjectMapper();
	}
}
