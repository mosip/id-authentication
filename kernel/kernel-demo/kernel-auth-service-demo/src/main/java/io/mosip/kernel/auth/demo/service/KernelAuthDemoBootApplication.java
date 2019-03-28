/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.auth.demo.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages= {"io.mosip.kernel.auth.demo.service","io.mosip.kernel.auth.*"})
public class KernelAuthDemoBootApplication {

	/**
	 * Main method for this application
	 * 
	 * @param args arguments to pass
	 */
	public static void main(String[] args) {
		SpringApplication.run(KernelAuthDemoBootApplication.class, args);
	}
}
