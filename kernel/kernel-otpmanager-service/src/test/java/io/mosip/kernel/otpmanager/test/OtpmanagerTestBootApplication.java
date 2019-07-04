package io.mosip.kernel.otpmanager.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Crypto manager application
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 *
 */
@SpringBootApplication(scanBasePackages = { "io.mosip.kernel.otpmanager.*" })
public class OtpmanagerTestBootApplication {

	/**
	 * Main method to run spring boot application
	 * 
	 * @param args args
	 */
	public static void main(String[] args) {
		SpringApplication.run(OtpmanagerTestBootApplication.class, args);
	}
}
