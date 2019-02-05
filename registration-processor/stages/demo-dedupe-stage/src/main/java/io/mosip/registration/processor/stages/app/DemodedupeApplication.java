package io.mosip.registration.processor.stages.app;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import io.mosip.registration.processor.stages.demodedupe.DemodedupeStage;

public class DemodedupeApplication{

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		AnnotationConfigApplicationContext configApplicationContext = new AnnotationConfigApplicationContext();
		configApplicationContext.scan("io.mosip.registration.processor.stages.config",
									  "io.mosip.registration.processor.demo.dedupe.config",
									  "io.mosip.registration.processor.status.config",
										"io.mosip.registration.processor.filesystem.ceph.adapter.impl.config",
										"io.mosip.registration.processor.packet.storage.config",
										"io.mosip.registration.processor.core.config",
										"io.mosip.registration.processor.core.kernel.beans");
		configApplicationContext.refresh();
		
		DemodedupeStage demodedupeStage = configApplicationContext.getBean(DemodedupeStage.class);
		demodedupeStage.deployVerticle();

	}
}