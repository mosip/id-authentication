package io.mosip.registration.processor.status;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

/**
 * The Class RegistrationStatusApplication.
 */
@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor.core",
		"io.mosip.registration.processor.status", "io.mosip.registration.processor.rest.client"})
public class RegistrationStatusApplication {

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(RegistrationStatusApplication.class, args);
	}
}
