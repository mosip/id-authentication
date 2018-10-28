package io.mosip.kernel.smsnotification.msg91;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

/**
 * Sms notification application.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@SpringBootApplication
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
public class SmsNotifierBootApplication {

	/**
	 * Main method to run spring boot application
	 * 
	 * @param args
	 *            params
	 */
	public static void main(String[] args) {
		SpringApplication.run(SmsNotifierBootApplication.class, args);
	}
}
