package io.mosip.kernel.idgenerator.registrationcenterid.test.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({ "io.mosip.kernel.*" })
public class RegistrationCenterIdGeneratorBootApplication {
	public static void main(String[] args) {
		SpringApplication.run(RegistrationCenterIdGeneratorBootApplication.class, args);
	}
}
