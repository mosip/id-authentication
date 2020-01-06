/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.cryptomanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Crypto-Manager-Service Boot Application
 * 
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
@SpringBootApplication(scanBasePackages = { "io.mosip.kernel.cryptomanager.*", "io.mosip.kernel.auth.*" })
public class CryptoManagerBootApplication {

	/**
	 * Main method for this application
	 * 
	 * @param args arguments to pass
	 */
	public static void main(String[] args) {
		SpringApplication.run(CryptoManagerBootApplication.class, args);
	}
}
