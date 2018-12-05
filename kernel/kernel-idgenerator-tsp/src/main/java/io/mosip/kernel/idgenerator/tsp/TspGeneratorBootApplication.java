package io.mosip.kernel.idgenerator.tsp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class for TSPID generation.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@SpringBootApplication
public class TspGeneratorBootApplication {

	/**
	 * Main method for TSPID generator.
	 * 
	 * @param args
	 *            the argument.
	 */
	public static void main(String[] args) {

		SpringApplication.run(TspGeneratorBootApplication.class, args);

	}
}
