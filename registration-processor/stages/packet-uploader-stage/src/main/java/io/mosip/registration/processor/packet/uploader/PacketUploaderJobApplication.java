package io.mosip.registration.processor.packet.uploader;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import io.mosip.registration.processor.packet.uploader.stage.PacketUploaderStage;

/**
 * The Class PacketUploaderJobApplication.
 */

public class PacketUploaderJobApplication {


	/**
	 * The main method.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		AnnotationConfigApplicationContext configApplicationContext = new AnnotationConfigApplicationContext();
		configApplicationContext.scan(
				  "io.mosip.registration.processor.packet.uploader.config",
				  "io.mosip.registration.processor.packet.manager.config",
				  "io.mosip.registration.processor.status.config",
				  "io.mosip.registration.processor.filesystem.ceph.adapter.impl.config",
				  "io.mosip.registration.processor.rest.client.config",
				  "io.mosip.registration.processor.core.kernel.beans");
		configApplicationContext.refresh();

		PacketUploaderStage packetUploaderStage = configApplicationContext.getBean(PacketUploaderStage.class);

		packetUploaderStage.deployVerticle();
	}

}