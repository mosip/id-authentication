package io.mosip.preregistration.login;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages="io.mosip.*")
public class PreRegistartionLoginApplication {
	public static void main(String[] args) {
		SpringApplication.run(PreRegistartionLoginApplication.class, args);
	}
}
