package io.mosip.kernel.tokenidgenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * TokenID Generator boot application
 * 
 * @author Urvil Joshi
 * @author Ritesh Sinha 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@SpringBootApplication(scanBasePackages = { "io.mosip.kernel.tokenidgenerator.*", "io.mosip.kernel.auth.*" })
public class TokenIDGeneratorBootApplication {
   
   /**
	 * Main method to run spring boot application
	 * 
	 * @param args
	 *            args
	 */
	public static void main(String[] args) {
		SpringApplication.run(TokenIDGeneratorBootApplication.class, args);
	}
		
}