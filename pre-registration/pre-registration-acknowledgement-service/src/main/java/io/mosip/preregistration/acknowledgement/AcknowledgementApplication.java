package io.mosip.preregistration.acknowledgement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "io.mosip.*")
public class AcknowledgementApplication {

	public static void main(String[] args) {
		SpringApplication.run(AcknowledgementApplication.class, args);
	}

}

