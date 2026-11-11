package io.mosip.authentication.service.internal.config;

import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Batch 6 no longer exposes a {@link JobRegistry} bean through Spring Boot's batch
 * auto-configuration (it is only created by {@code @EnableBatchProcessing}). The credential store
 * jobs look the registry up, so it is declared explicitly here.
 */
@Configuration
public class BatchJobRegistryConfig {

	@Bean
	public JobRegistry jobRegistry() {
		return new MapJobRegistry();
	}
}
