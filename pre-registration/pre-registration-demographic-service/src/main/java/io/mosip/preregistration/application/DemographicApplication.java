package io.mosip.preregistration.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author M1037717
 *
 * version 1.0
 */
@SpringBootApplication
@ComponentScan(basePackages = "io.mosip.*")
public class DemographicApplication {
	/**
	 * Method to start the demographic API service
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(DemographicApplication.class, args);
	}
}
