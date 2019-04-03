package io.mosip.registrationprocessor.dummyservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

/**
 * Dummy Rest service application
 *
 */
@SpringBootApplication
@PropertySource("classpath:bootstrap.properties")
public class DummyRestServiceApplication {

	/**
	 * main method to load dummy service
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(DummyRestServiceApplication.class, args);
	}

}
