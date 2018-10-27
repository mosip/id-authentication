package io.mosip.kernel.idgenerator.uin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Main class of Uin Generator Application. This will run batch job to generate
 * and store uins in database and a web service to fetch a uin
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@SpringBootApplication
@ComponentScan(basePackages = "io.mosip.*")
public class UinGeneratorBootApplication {
	/**
	 * Function to run the Uin generator application
	 * 
	 * @param args
	 *            The arguments to pass will executing the main function
	 */
	public static void main(String[] args) {
		SpringApplication.run(UinGeneratorBootApplication.class, args);
	}
}