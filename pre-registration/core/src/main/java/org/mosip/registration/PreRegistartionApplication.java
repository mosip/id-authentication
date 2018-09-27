package org.mosip.registration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource(value = {"classpath:application.properties"})
public class PreRegistartionApplication {
	public static void main(String[] args) {
		SpringApplication.run(PreRegistartionApplication.class, args);
	}
}
