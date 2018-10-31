package io.mosip.preregistration.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages="io.mosip.*")
public class PreRegistartionAuthApplication {
	public static void main(String[] args) {
		SpringApplication.run(PreRegistartionAuthApplication.class, args);
	}
}
