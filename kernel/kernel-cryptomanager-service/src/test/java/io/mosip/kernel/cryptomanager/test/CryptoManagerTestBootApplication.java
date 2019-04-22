package io.mosip.kernel.cryptomanager.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Crypto manager application
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 *
 */
@SpringBootApplication(scanBasePackages = { "io.mosip.kernel.cryptomanager.*" })
public class CryptoManagerTestBootApplication {

	/**
	 * Main method to run spring boot application
	 * 
	 * @param args args
	 */
	public static void main(String[] args) {
		SpringApplication.run(CryptoManagerTestBootApplication.class, args);
	}
}
