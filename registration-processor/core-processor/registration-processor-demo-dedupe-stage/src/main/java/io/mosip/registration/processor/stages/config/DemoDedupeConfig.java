package io.mosip.registration.processor.stages.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.mosip.registration.processor.stages.demodedupe.BiometricValidation;
import io.mosip.registration.processor.stages.demodedupe.DemoDedupe;
import io.mosip.registration.processor.stages.demodedupe.DemodedupeProcessor;
import io.mosip.registration.processor.stages.demodedupe.DemodedupeStage;

@Configuration
public class DemoDedupeConfig {

	@Bean
	public DemodedupeStage getDemoDedupeStage() {
		return new DemodedupeStage();
	}

	@Bean
	public DemoDedupe getDemoDedupe() {
		return new DemoDedupe();
	}

	@Bean
	public BiometricValidation getBiometricValidation() {
		return new BiometricValidation();
	}

	@Bean
	public DemodedupeProcessor getDemodedupeProcessor() {
		return new DemodedupeProcessor();
	}
}
