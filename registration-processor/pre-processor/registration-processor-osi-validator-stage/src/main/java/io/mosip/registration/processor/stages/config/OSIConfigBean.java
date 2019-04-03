package io.mosip.registration.processor.stages.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.mosip.registration.processor.stages.osivalidator.OSIValidator;
import io.mosip.registration.processor.stages.osivalidator.OSIValidatorStage;
import io.mosip.registration.processor.stages.osivalidator.UMCValidator;
import io.mosip.registration.processor.stages.osivalidator.utils.OSIUtils;

@Configuration
public class OSIConfigBean{

	@Bean
	public OSIValidator getOSIValidator() {
		return new OSIValidator();
	}
	
	@Bean
	public UMCValidator getUMCValidator() {
		return new UMCValidator();
	}
	@Bean
	public OSIValidatorStage getOSIValidatorStage() {
		return new OSIValidatorStage();
	}
	
	@Bean
	public OSIUtils getOSIUtils() {
		return new OSIUtils();
	}
}
