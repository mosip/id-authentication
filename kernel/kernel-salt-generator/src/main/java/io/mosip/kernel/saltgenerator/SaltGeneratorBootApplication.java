package io.mosip.kernel.saltgenerator;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * The Class SaltGeneratorBootApplication - Salt generator Job is a
 * one-time job which populates salts for hashing and encrypting data.
 *
 * @author Manoj SP
 */
@SpringBootApplication
@EnableBatchProcessing
public class SaltGeneratorBootApplication {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		ApplicationContext applicationContext = SpringApplication.run(SaltGeneratorBootApplication.class,
				args);
		SpringApplication.exit(applicationContext);
	}

}
