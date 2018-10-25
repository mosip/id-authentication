package io.mosip.registration.processor.status;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The Class RegistrationStatusApplication.
 */
@SpringBootApplication
// @PropertySource({ "classpath:status-application.properties" })
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
