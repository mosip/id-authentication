package io.mosip.registration.processor.stages;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import io.mosip.registration.processor.stages.packet.validator.PacketValidatorStage;

/**
 * The Class PacketValidatorApplication.
 */

public class PacketValidatorApplication {

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.scan("io.mosip.registration.processor.stages.config",
				"io.mosip.registration.processor.status.config",
				"io.mosip.registration.processor.rest.client.config",
				"io.mosip.registration.processor.filesystem.ceph.adapter.impl.config",
				"io.mosip.registration.processor.packet.storage.config",
				"io.mosip.registration.processor.core.config",
				"io.mosip.registration.processor.core.kernel.beans");
		ctx.refresh();
		PacketValidatorStage packetValidatorStage = ctx.getBean(PacketValidatorStage.class);
		packetValidatorStage.deployVerticle();
	}
}
