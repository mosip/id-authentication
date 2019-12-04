/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;

import io.mosip.kernel.idobjectvalidator.impl.IdObjectCompositeValidator;

/**
 * This class is used to define the start of the demographic service
 * 
 * @author Rajath KR
 * @since 1.0.0
 */
@SpringBootApplication(scanBasePackages= {"io.mosip.preregistration.core.*,io.mosip.preregistration.document.*,io.mosip.preregistration.application.*,io.mosip.kernel.emailnotifier.*,io.mosip.kernel.smsnotifier.*,io.mosip.kernel.cryotomanager.*,io.mosip.kernel.auditmanger.*,io.mosip.kernel.idgenerator.*"})
@ComponentScan(basePackages = "io.mosip.*", excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "io.mosip.kernel.idobjectvalidator.*"))
public class DemographicTestApplication {
	/**
	 * 
	 * @param args Unused
	 */
	public static void main(String[] args) {
		SpringApplication.run(DemographicTestApplication.class, args);
	}
}
