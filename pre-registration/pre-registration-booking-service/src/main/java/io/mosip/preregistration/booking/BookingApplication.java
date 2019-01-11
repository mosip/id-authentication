/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.booking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * This class is used to define the start of the Booking application.
 * 
 * @author Kishan Rathore
 * @author Jagadishwari
 * @author Ravi C. Balaji
 * @since 1.0.0
 *
 */
@SpringBootApplication
@ComponentScan(basePackages = "io.mosip.*")
public class BookingApplication {
	/**
	 * Method to start the Booking API service
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(BookingApplication.class, args);
	}
}
