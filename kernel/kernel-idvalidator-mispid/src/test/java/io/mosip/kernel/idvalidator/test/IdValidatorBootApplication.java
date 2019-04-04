package io.mosip.kernel.idvalidator.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/*
 * (non-Javadoc)
 * IdValidator Boot Application for SpringBootTest
 */

@SpringBootApplication
@ComponentScan(basePackages = "io.mosip.kernel.*")
public class IdValidatorBootApplication {
	public static void main(String[] args) {
		SpringApplication.run(IdValidatorBootApplication.class, args);
	}
}
