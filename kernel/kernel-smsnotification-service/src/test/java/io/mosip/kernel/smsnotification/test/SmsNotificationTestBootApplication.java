package io.mosip.kernel.smsnotification.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Sms notification application.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@SpringBootApplication(scanBasePackages = { "io.mosip.kernel.smsnotification.*" })
public class SmsNotificationTestBootApplication {

	/**
	 * Main method to run spring boot application
	 * 
	 * @param args args
	 */
	public static void main(String[] args) {
		SpringApplication.run(SmsNotificationTestBootApplication.class, args);
	}
}
