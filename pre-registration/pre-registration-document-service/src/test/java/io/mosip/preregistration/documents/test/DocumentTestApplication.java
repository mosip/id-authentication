package io.mosip.preregistration.documents.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * This class is used to define the start of the document service
 * 
 * @author Tapaswini Behera
 * @since 1.0.0
 */
@SpringBootApplication(scanBasePackages= {"io.mosip.preregistration.core.*,io.mosip.preregistration.documents.*"
		+ ",io.mosip.preregistration.application.*"+",io.mosip.preregistration.booking.*"+ ",io.mosip.kernel.emailnotifier.*,"
				+ "io.mosip.kernel.smsnotifier.*,io.mosip.kernel.cryotomanager.*,io.mosip.kernel.auditmanger.*,io.mosip.kernel.idgenerator.*"})
//@ComponentScan(basePackages = "io.mosip.*")
public class DocumentTestApplication {
	/**
	 * 
	 * @param args Unused
	 */
	public static void main(String[] args) {
		SpringApplication.run(DocumentTestApplication.class, args);
	}
}
