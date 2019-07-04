package io.mosip.kernel.signature;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

/**
 * Crypto signature application
 * 
 * @author Uday Kumar
 * @since 1.0.0
 *
 */
@SpringBootApplication(scanBasePackages = {"io.mosip.kernel.signature.*" ,"io.mosip.kernel.auth.*" })
public class SignatureBootApplication {

	/**
	 * Main method to run spring boot application
	 * 
	 * @param args
	 *            args
	 */
	public static void main(String[] args) {
		SpringApplication.run(SignatureBootApplication.class, args);
	}
}
