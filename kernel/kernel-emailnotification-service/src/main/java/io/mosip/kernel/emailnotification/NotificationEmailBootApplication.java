package io.mosip.kernel.emailnotification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Mail notifier application
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@SpringBootApplication
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
@EnableAsync
public class NotificationEmailBootApplication {
	
	/**
	 * Main method to run spring boot application
	 * 
	 * @param args the argument
	 */
	public static void main(String[] args) {
		SpringApplication.run(NotificationEmailBootApplication.class, args);
	}
}
