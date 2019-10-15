package io.mosip.preregistration.document;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


/**
 * This class is used to define the start of the Document Service Impl
 * 
 * @author Kishan Rathore
 *
 */
@SpringBootApplication
@ComponentScan(basePackages = "io.mosip.*")
public class DocumentServiceImpl 
{
	/**
	 * @param args noUsed
	 */
	public static void main(String[] args) {
		SpringApplication.run(DocumentServiceImpl.class, args);
	}
}
