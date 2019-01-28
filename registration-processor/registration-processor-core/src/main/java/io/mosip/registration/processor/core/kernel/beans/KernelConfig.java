package io.mosip.registration.processor.core.kernel.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.mosip.kernel.core.idvalidator.spi.RidValidator;
import io.mosip.kernel.idvalidator.rid.impl.RidValidatorImpl;

@Configuration
public class KernelConfig {

	@Bean
	public RidValidator<String> getRidValidator(){
		return new RidValidatorImpl();
	}
	
}
