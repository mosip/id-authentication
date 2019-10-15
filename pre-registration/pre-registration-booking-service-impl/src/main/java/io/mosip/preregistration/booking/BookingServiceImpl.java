package io.mosip.preregistration.booking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * This class is used to define the start of the Booking service impl.
 * 
 * @author Kishan Rathore
 *
 */
@SpringBootApplication
@ComponentScan(basePackages = "io.mosip.*")
public class BookingServiceImpl 
{
	/**
	 * Method to start the Booking API service
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(BookingServiceImpl.class, args);
	}
}
