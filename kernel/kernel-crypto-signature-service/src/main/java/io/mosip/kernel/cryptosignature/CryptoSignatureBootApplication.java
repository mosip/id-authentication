package io.mosip.kernel.cryptosignature;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Crypto signature application
 * 
 * @author Uday Kumar
 * @since 1.0.0
 *
 */
@SpringBootApplication(scanBasePackages = { "io.mosip.kernel.cryptosignature.*" })
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
