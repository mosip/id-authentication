package io.mosip.idrepository.vid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * The Class IdRepoVidApplication.
 *
 * @author Prem Kumar
 */
@SpringBootApplication
@ComponentScan("io.mosip.*")
public class VidBootApplication {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(VidBootApplication.class, args);
	}
}
