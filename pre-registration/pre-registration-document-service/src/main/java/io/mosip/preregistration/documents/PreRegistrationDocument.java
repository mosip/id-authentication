package io.mosip.preregistration.documents;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages="io.mosip.*")
public class PreRegistrationDocument {
	public static void main(String[] args) {
		SpringApplication.run(PreRegistrationDocument.class, args);
	}
}
