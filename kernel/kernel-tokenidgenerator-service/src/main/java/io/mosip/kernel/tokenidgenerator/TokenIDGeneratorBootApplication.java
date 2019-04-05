package io.mosip.kernel.tokenidgenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * VID Generator boot application
 * 
 * @author Urvil Joshi
 * @author Ritesh Sinha 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@SpringBootApplication
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