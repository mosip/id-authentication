package org.mosip.kernel.auditmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Audit manager application
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@SpringBootApplication
public class AuditManagerApp {

	/**
	 * Main method to run spring boot application
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(AuditManagerApp.class, args);
	}
}
