package io.mosip.preregistration.demographic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * This class is used to define the start of the demographic service
 * 
 * @author Rajath KR
 * @since 1.0.0
 */
@SpringBootApplication(scanBasePackages= {"io.mosip.preregistration.*","io.mosip.kernel.auth.*"})
public class DemographicServiceImpl {
	/**
	 * 
	 * @param args
	 *            Unused
	 */
	public static void main(String[] args) {
		SpringApplication.run(DemographicServiceImpl.class, args);
	}
}
