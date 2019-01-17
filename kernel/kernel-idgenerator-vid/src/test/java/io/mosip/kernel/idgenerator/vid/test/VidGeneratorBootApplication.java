package io.mosip.kernel.idgenerator.vid.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/*
 * (non-Javadoc)
 * VidGenerator Boot Application for SpringBootTest
 */

@SpringBootApplication
@ComponentScan(basePackages = "io.mosip.kernel.*")
public class VidGeneratorBootApplication {
	public static void main(String[] args) {
		SpringApplication.run(VidGeneratorBootApplication.class, args);
	}
}
