package io.mosip.registration.processor.message.sender;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import io.mosip.registration.processor.message.sender.stage.MessageSenderStage;

/**
 * The Class MessageSenderApplication.
 *
 * @author Alok Ranjan
 * @since 1.0.0
 *
 */
public class MessageSenderApplication {
	/**
	 * Main method to instantiate the spring boot application.
	 *
	 * @param args
	 *            the command line arguments
	 */
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		
		AnnotationConfigApplicationContext ctx= new AnnotationConfigApplicationContext();
		ctx.scan("io.mosip.registration.processor.stages.uingenerator.config",
				"io.mosip.registration.processor.status.config", 
				"io.mosip.registration.processor.filesystem.ceph.adapter.impl.config",
				"io.mosip.registration.processor.rest.client.config",
				"io.mosip.registration.processor.packet.storage.config",
				"io.mosip.registration.processor.core.config",
				"io.mosip.registration.processor.stages.config",
				"io.mosip.registration.processor.message.sender.config",
				"io.mosip.registration.processor.core.kernel.beans");
		
		ctx.refresh();
		
		MessageSenderStage messageSenderStage = ctx.getBean( MessageSenderStage.class );
		messageSenderStage.deployVerticle();
		
	}
	
}