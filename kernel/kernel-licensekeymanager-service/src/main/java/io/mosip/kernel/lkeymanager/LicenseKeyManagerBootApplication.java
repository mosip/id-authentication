package io.mosip.kernel.lkeymanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * -------------------------------------------------------------------------
 * LICENSEKEY-MANAGER-SERVICE APPLICATION
 * -------------------------------------------------------------------------
 * This service serves the functionality of License Generation, License
 * Permission Mapping, Fetching License Permissions.
 * -------------------------------------------------------------------------
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
	 * @param args the input arguments.
	 */
	public static void main(String[] args) {
		SpringApplication.run(LicenseKeyManagerBootApplication.class, args);
	}
}
