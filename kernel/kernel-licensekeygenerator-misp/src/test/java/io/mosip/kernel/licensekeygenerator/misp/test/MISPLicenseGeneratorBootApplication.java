package io.mosip.kernel.licensekeygenerator.misp.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({ "io.mosip.kernel.*" })
public class MISPLicenseGeneratorBootApplication {
	public static void main(String[] args) {
		SpringApplication.run(MISPLicenseGeneratorBootApplication.class, args);
	}
}