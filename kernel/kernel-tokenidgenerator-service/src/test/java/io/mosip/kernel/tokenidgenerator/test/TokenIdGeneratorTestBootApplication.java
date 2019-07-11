package io.mosip.kernel.tokenidgenerator.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = { "io.mosip.kernel.tokenidgenerator.*" })
public class TokenIdGeneratorTestBootApplication {

	/**
	 * Main method to run spring boot application
	 * 
	 * @param args args
	 */
	public static void main(String[] args) {
		SpringApplication.run(TokenIdGeneratorTestBootApplication.class, args);
	}
}
