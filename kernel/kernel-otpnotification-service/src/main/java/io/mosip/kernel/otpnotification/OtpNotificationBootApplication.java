package io.mosip.kernel.otpnotification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

/**
 * -------------------------------------------------------------------------
 * OTP-NOTIFICATION-SERVICE APPLICATION
 * -------------------------------------------------------------------------
 * This orchestrated service serves the functionality to notify the OTP
 * generated from kernel-otpmanager-service, either through sms, email or both.
 * -------------------------------------------------------------------------
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@SpringBootApplication(scanBasePackages = { "io.mosip.kernel.otpnotification.*", "io.mosip.kernel.auth.*" })
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
public class OtpNotificationBootApplication {
	/**
	 * Main method to run spring boot application
	 * 
	 * @param args the argument
	 */
	public static void main(String[] args) {
		SpringApplication.run(OtpNotificationBootApplication.class, args);
	}
}
