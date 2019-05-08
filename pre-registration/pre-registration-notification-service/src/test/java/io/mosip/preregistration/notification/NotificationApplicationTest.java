package io.mosip.preregistration.notification;



import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;


/**
 * Main class for NotificationApplication.
 * 
 * @author Akshay
 * @since 1.0.0
 *
 */
@SpringBootApplication(scanBasePackages= {"io.mosip.preregistration.core.*"
		+ ",io.mosip.preregistration.notification.*"	+ ",io.mosip.kernel.templatemanager.velocity.*,io.mosip.kernel.qrcode.generator.zxing.*"})
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class }) 
public class NotificationApplicationTest {

	/**
	 * Main method for NotificationApplication.
	 * 
	 * @param args
	 *            the arguments.
	 */
	public static void main(String[] args) {
		SpringApplication.run(NotificationApplicationTest.class, args);
	}

}
