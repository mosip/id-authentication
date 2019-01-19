package io.mosip.kernel.datamapper.orika.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/*
 * (non-Javadoc)
 * Data Validator Boot Application for SpringBootTest
 */
@SpringBootApplication
@ComponentScan(basePackages = "io.mosip.kernel.*")
public class DataMapperBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataMapperBootApplication.class, args);

	}
}
