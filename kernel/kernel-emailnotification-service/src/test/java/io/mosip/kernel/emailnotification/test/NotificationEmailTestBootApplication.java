package io.mosip.kernel.emailnotification.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Mail notifier application
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@SpringBootApplication(scanBasePackages = { "io.mosip.kernel.emailnotification.*" })
public class NotificationEmailTestBootApplication {

	/**
	 * Main method to run spring boot application
	 * 
	 * @param args args
	 */
	public static void main(String[] args) {
		SpringApplication.run(NotificationEmailTestBootApplication.class, args);
	}
}
