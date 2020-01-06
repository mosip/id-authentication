package io.mosip.kernel.applicanttype.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = { "io.mosip.kernel.applicanttype.*" })
public class ApplicantTypeTestBootApplication {

	/**
	 * Main method to run spring boot application
	 * 
	 * @param args args
	 */
	public static void main(String[] args) {
		SpringApplication.run(ApplicantTypeTestBootApplication.class, args);
	}
}
