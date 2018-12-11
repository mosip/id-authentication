package io.mosip.preregistration.batchjobservices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


/**
 * @author M1043008
 *
 */
@SpringBootApplication
@ComponentScan(basePackages="io.mosip.*")
public class PreRegistrationBatchService {
	public static void main(String[] args) {
		SpringApplication.run(PreRegistrationBatchService.class, args);
	}
}
