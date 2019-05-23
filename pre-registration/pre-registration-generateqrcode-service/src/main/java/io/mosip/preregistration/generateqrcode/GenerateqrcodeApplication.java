package io.mosip.preregistration.generateqrcode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * This class is used to define the start of the QR code service 
 * 
 * @author Sanober Noor
 * @since 1.0.0
 */
@SpringBootApplication
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class }) 
@ComponentScan(basePackages = "io.mosip.*")
public class GenerateqrcodeApplication {

	public static void main(String[] args) {
		SpringApplication.run(GenerateqrcodeApplication.class, args);
	}

}
