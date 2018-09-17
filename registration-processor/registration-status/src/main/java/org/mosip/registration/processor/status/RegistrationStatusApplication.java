package org.mosip.registration.processor.status;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource({"classpath:status-application.properties"})
public class RegistrationStatusApplication {
	public static void main(String[] args) {
		SpringApplication.run(RegistrationStatusApplication.class, args);
	}
}
