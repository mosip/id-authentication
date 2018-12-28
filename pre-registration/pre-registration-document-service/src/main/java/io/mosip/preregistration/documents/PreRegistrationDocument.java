/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.documents;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * This class is used to define the start of the Document service
 * 
 * @author Rajath KR
 * @since 1.0.0
 */
@SpringBootApplication
@ComponentScan(basePackages = "io.mosip.*")
public class PreRegistrationDocument {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(PreRegistrationDocument.class, args);
	}
}
