package io.mosip.preregistration.generateQRcode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class }) 
@ComponentScan(basePackages = "io.mosip.*")
public class GenerateQRcodeApplication {

	public static void main(String[] args) {
		SpringApplication.run(GenerateQRcodeApplication.class, args);
	}

}
