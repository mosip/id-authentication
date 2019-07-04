package io.mosip.kernel.masterdata.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class of Sync handler Application.
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 */
@SpringBootApplication(scanBasePackages = "io.mosip.kernel.masterdata.*")
//@Profile("test")
//@Import(TestSecurityConfig.class)
public class TestBootApplication {
	/**
	 * Function to run the Master-Data-Service application
	 * 
	 * @param args The arguments to pass will executing the main function
	 */
	public static void main(String[] args) {
		SpringApplication.run(TestBootApplication.class, args);
	}
}