package io.mosip.registrationprocessor.eis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

/**
 * external integration service application
 *
 */
@SpringBootApplication
@PropertySource("classpath:bootstrap.properties")
public class ExternalIntegrationServiceApplication {

	/**
	 *load main external integration service
	 */
	public static void main(String[] args) {
		SpringApplication.run(ExternalIntegrationServiceApplication.class, args);
	}

}
