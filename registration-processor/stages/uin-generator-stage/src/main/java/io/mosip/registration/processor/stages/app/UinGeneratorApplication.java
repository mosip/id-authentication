package io.mosip.registration.processor.stages.app;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import io.mosip.registration.processor.stages.uigenerator.UinGeneratorStage;

/*@SpringBootApplication(scanBasePackages = { 
		"io.mosip.registration.processor.status", "io.mosip.registration.processor.filesystem.ceph.adapter.impl",
		"io.mosip.registration.processor.rest.client","io.mosip.registration.processor.packet.storage",
		"io.mosip.registration.processor.core","io.mosip.registration.processor.stages","io.mosip.registration.processor.message.sender"})*/
public class UinGeneratorApplication {


	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		//SpringApplication.run(UinGeneratorApplication.class, args);
		
		AnnotationConfigApplicationContext configApplicationContext = new AnnotationConfigApplicationContext();
		configApplicationContext.scan("io.mosip.registration.processor.stages.uigenerator.config",
				"io.mosip.registration.processor.status.config", 
				"io.mosip.registration.processor.filesystem.ceph.adapter.impl.config",
				"io.mosip.registration.processor.rest.client.config",
				"io.mosip.registration.processor.packet.storage.config",
				"io.mosip.registration.processor.core.config",
				"io.mosip.registration.processor.stages.config",
				"io.mosip.registration.processor.message.sender.config",
				"io.mosip.registration.processor.core.kernel.beans");
		
		configApplicationContext.refresh();
		
		
		UinGeneratorStage uinGeneratorStage = configApplicationContext.getBean(UinGeneratorStage.class);
		uinGeneratorStage.deployVerticle();
	}

	
}