package io.mosip.kernel.signature;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Crypto signature application
 * 
 * @author Uday Kumar
 * @since 1.0.0
 *
 */
@SpringBootApplication(scanBasePackages = {"io.mosip.kernel.signature.*" ,"io.mosip.kernel.auth.*" })
public class CryptoSignatureBootApplication {

	/**
	 * Main method to run spring boot application
	 * 
	 * @param args
	 *            args
	 */
	public static void main(String[] args) {
		SpringApplication.run(CryptoSignatureBootApplication.class, args);
	}
}
