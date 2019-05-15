package io.mosip.kernel.cryptosignature.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * crypto signature application
 * 
 * @author uday kumar
 * @since 1.0.0
 *
 */
@SpringBootApplication(scanBasePackages = { "io.mosip.kernel.cryptosignature.*" })
public class CryptoSignatureTestBootApplication {

	/**
	 * Main method to run spring boot application
	 * 
	 * @param args args
	 */
	public static void main(String[] args) {
		SpringApplication.run(CryptoSignatureTestBootApplication.class, args);
	}
}
