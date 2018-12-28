/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * This class is used to define the start of the demographic service
 * 
 * @author Rajath KR
 * @since 1.0.0
 */
@SpringBootApplication
@ComponentScan(basePackages = "io.mosip.*")
public class DemographicApplication {
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(DemographicApplication.class, args);
	}
}
