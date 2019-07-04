/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.batchjob;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * This class is used to define the start of the Booking application.
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */
@SpringBootApplication
@ComponentScan(basePackages = "io.mosip.*")
public class PreRegistrationBatchJob {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(PreRegistrationBatchJob.class, args);
	}
}
