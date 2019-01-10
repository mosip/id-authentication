package io.mosip.preregistration.batchjobservices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


/**
 * @author Kishan rathore
 * @since 1.0.0
 *
 * This class is used to define the start of the PreRegistration batch Job.
 */
@SpringBootApplication
@ComponentScan(basePackages="io.mosip.*")
public class PreRegistrationBatchService {
	public static void main(String[] args) {
		SpringApplication.run(PreRegistrationBatchService.class, args);
	}
}
