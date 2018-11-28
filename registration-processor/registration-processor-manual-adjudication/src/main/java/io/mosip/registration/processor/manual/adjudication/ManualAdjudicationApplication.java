package io.mosip.registration.processor.manual.adjudication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The Class ManualAdjudicationApplication
 */
@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor.packet.receiver" })

public class ManualAdjudicationApplication {

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(ManualAdjudicationApplication.class, args);
	}
}
