package io.mosip.kernel.datavalidator.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/*
 * (non-Javadoc)
 * Data Validator Boot Application for SpringBootTest
 */
@SpringBootApplication
@ComponentScan(basePackages = "io.mosip.kernel.*")
public class DataValidatorBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataValidatorBootApplication.class, args);

	}
}
