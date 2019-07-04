package io.mosip.preregistration.generateqrcode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(scanBasePackages= {"io.mosip.preregistration.core.*"
		+ ",io.mosip.preregistration.generateqrcode.*"	+ ",io.mosip.kernel.templatemanager.velocity.*,io.mosip.kernel.qrcode.generator.zxing.*"})
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class }) 
public class GenerateQRcodeApplicationTests {

	
	/**
	 * Main method for GenerateQRcodeApplicatiion.
	 * 
	 * @param args
	 *            the arguments.
	 */
	public static void main(String[] args) {
		SpringApplication.run(GenerateQRcodeApplicationTests.class, args);
	}
}
