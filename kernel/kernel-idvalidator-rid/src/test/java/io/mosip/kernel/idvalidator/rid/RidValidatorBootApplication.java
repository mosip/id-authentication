package io.mosip.kernel.idvalidator.rid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/*
 * (non-Javadoc)
 * IdValidator Boot Application for SpringBootTest
 */

@SpringBootApplication
@ComponentScan(basePackages = "io.mosip.kernel.*")
public class RidValidatorBootApplication {
	public static void main(String[] args) {
		SpringApplication.run(RidValidatorBootApplication.class, args);
	}
}
