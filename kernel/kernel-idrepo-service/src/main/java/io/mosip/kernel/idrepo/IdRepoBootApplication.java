package io.mosip.kernel.idrepo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * The Class IdRepoApplication.
 *
 * @author Manoj SP
 */
@SpringBootApplication
@ComponentScan("io.mosip.*")
public class IdRepoBootApplication {

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(IdRepoBootApplication.class, args);
	}
}
