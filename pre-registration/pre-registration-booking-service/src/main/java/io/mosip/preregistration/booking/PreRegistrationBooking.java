package io.mosip.preregistration.booking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author M1046129
 *
 */
@SpringBootApplication
@ComponentScan(basePackages = "io.mosip.*")
public class PreRegistrationBooking {
	public static void main(String[] args) {
		SpringApplication.run(PreRegistrationBooking.class, args);
	}
}
