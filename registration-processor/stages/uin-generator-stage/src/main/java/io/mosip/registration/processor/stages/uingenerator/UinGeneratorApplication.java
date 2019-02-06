package io.mosip.registration.processor.stages.uingenerator;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import io.mosip.registration.processor.stages.uingenerator.stage.UinGeneratorStage;

public class UinGeneratorApplication {


	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		
		AnnotationConfigApplicationContext configApplicationContext = new AnnotationConfigApplicationContext();
		configApplicationContext.scan("io.mosip.registration.processor.stages.uingenerator.config",
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