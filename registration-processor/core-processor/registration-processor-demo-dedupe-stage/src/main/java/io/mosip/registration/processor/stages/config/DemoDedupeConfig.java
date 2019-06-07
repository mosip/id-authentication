package io.mosip.registration.processor.stages.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.mosip.registration.processor.stages.demodedupe.DemoDedupe;
import io.mosip.registration.processor.stages.demodedupe.DemodedupeProcessor;
import io.mosip.registration.processor.stages.demodedupe.DemoDedupeStage;

@Configuration
public class DemoDedupeConfig {

	@Bean
	public DemoDedupeStage getDemoDedupeStage() {
		return new DemoDedupeStage();
	}

	@Bean
	public DemoDedupe getDemoDedupe() {
		return new DemoDedupe();
	}

	@Bean
	public DemodedupeProcessor getDemodedupeProcessor() {
		return new DemodedupeProcessor();
	}
}
