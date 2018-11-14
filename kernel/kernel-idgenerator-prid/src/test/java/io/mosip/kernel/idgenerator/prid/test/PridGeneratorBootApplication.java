package io.mosip.kernel.idgenerator.prid.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/*
 * (non-Javadoc)
 * PridGenerator Boot Application for SpringBootTest
 */

@SpringBootApplication
@ComponentScan(basePackages = "io.mosip.kernel.*")
public class PridGeneratorBootApplication {
	public static void main(String[] args) {
		SpringApplication.run(PridGeneratorBootApplication.class, args);
	}
}
