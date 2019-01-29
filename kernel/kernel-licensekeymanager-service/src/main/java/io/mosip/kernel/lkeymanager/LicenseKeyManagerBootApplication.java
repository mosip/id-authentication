package io.mosip.kernel.lkeymanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot Runner class for LicenseKeyManager Service.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@SpringBootApplication
public class LicenseKeyManagerBootApplication {
	/**
	 * Main method
	 * 
	 * @param args
	 *            the input arguments.
	 */
	public static void main(String[] args) {
		SpringApplication.run(LicenseKeyManagerBootApplication.class, args);
	}
}
