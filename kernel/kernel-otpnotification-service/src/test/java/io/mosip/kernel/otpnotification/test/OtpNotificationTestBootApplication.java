package io.mosip.kernel.otpnotification.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Crypto manager application
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 *
 */
@SpringBootApplication(scanBasePackages = { "io.mosip.kernel.otpnotification.*" })
public class OtpNotificationTestBootApplication {

	/**
	 * Main method to run spring boot application
	 * 
	 * @param args args
	 */
	public static void main(String[] args) {
		SpringApplication.run(OtpNotificationTestBootApplication.class, args);
	}
}
