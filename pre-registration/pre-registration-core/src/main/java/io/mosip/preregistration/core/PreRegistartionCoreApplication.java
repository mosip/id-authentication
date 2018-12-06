package io.mosip.preregistration.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/*
 * (non-Javadoc)
 * 
 *
 */

@SpringBootApplication
@ComponentScan(basePackages = "io.mosip.*")
public class PreRegistartionCoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(PreRegistartionCoreApplication.class, args);
	}
}
