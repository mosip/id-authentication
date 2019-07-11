package io.mosip.registration.processor.retry.verticle.config;

import org.springframework.context.annotation.Bean;

import io.mosip.registration.processor.retry.verticle.stages.RetryStage;

public class RetyrVerticleConfig {

	@Bean
	public RetryStage getRetryStage() {
		return new RetryStage();
	}
}
