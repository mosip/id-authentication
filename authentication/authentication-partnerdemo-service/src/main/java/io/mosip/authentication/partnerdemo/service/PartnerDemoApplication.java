package io.mosip.authentication.partnerdemo.service;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import io.mosip.authentication.partnerdemo.service.config.SwaggerConfig;
import io.mosip.kernel.templatemanager.velocity.impl.TemplateManagerImpl;

/**
 * Spring-boot class for ID Authentication Application.
 *
 * @author Dinesh Karuppiah
 */
@SpringBootApplication
@Import(value = {  TemplateManagerImpl.class, VelocityEngine.class, SwaggerConfig.class,
		 })
public class PartnerDemoApplication {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(PartnerDemoApplication.class, args);
	}

}
