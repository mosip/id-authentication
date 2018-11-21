package io.mosip.pregistration.datasync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Data sync Application
 * 
 * @author M1046129 - Jagadishwari
 *
 */
@SpringBootApplication
@ComponentScan(basePackages = "io.mosip.*")
public class PreRegistrationDatasyncApplication {

	public static void main(String[] args) {
		SpringApplication.run(PreRegistrationDatasyncApplication.class, args);
	}
}
