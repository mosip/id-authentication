package io.mosip.authentication.partner.demo.service;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import io.mosip.kernel.jsonvalidator.impl.JsonSchemaLoader;
import io.mosip.kernel.jsonvalidator.impl.JsonValidatorImpl;
import io.mosip.kernel.templatemanager.velocity.impl.TemplateManagerImpl;

/**
 * Spring-boot class for ID Authentication Application.
 *
 * @author Dinesh Karuppiah
 */
@SpringBootApplication
@Import (value = {JsonValidatorImpl.class,TemplateManagerImpl.class,VelocityEngine.class, JsonSchemaLoader.class})
public class IdAuthenticationDemoApplication {
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(IdAuthenticationDemoApplication.class, args);
	}

}
