package io.mosip.registration.processor.bio.dedupe.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The Class BioDedupeApiApp.
 */
@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor" })
public class BioDedupeApiApp {

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(BioDedupeApiApp.class, args);
	}
}
