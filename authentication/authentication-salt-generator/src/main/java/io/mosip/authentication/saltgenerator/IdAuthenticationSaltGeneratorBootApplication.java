package io.mosip.authentication.saltgenerator;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * The Class IdAuthenticationSaltGeneratorBootApplication - Salt generator Job is a
 * one-time job which populates salts for hashing and encrypting UIN.
 *
 * @author Manoj SP
 */
@SpringBootApplication
@EnableBatchProcessing
public class IdAuthenticationSaltGeneratorBootApplication {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		ApplicationContext applicationContext = SpringApplication.run(IdAuthenticationSaltGeneratorBootApplication.class,
				args);
		SpringApplication.exit(applicationContext);
	}

}
