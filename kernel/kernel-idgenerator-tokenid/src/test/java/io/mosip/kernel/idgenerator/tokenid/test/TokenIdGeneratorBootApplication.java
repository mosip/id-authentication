package io.mosip.kernel.idgenerator.tokenid.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Test Boot Application for Token ID Generator.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@SpringBootApplication
@ComponentScan(basePackages = "io.mosip.kernel.*")
public class TokenIdGeneratorBootApplication {
	public static void main(String[] args) {
		SpringApplication.run(TokenIdGeneratorBootApplication.class, args);
	}
}
