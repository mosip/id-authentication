package io.mosip.demo.authentication.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import io.mosip.kernel.jsonvalidator.impl.JsonValidatorImpl;

/**
 * Spring-boot class for ID Authentication Application.
 *
 * @author Dinesh Karuppiah
 */
@SpringBootApplication
@Import (JsonValidatorImpl.class)
public class IdAuthenticationDemoApplication {
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(IdAuthenticationDemoApplication.class, args);
	}

}
