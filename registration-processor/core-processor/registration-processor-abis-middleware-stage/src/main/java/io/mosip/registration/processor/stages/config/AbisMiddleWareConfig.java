package io.mosip.registration.processor.stages.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.mosip.registartion.processor.abis.middleware.stage.AbisMiddleWareStage;


@Configuration
public class AbisMiddleWareConfig {
	
	@Bean
	public AbisMiddleWareStage getAbisMiddleWareStage() {
		return new AbisMiddleWareStage();
	}

}
