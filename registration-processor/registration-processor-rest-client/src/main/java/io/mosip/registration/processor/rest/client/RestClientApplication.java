package io.mosip.registration.processor.rest.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The Class RestClientApplication.
 */
@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor.core",
		"io.mosip.registration.processor.rest.client" })
public class RestClientApplication {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	@Autowired
	public static void main(String[] args) {
		SpringApplication.run(RestClientApplication.class, args);
	}
	
}
