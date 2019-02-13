package io.mosip.registration.processor.print;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author M1048399
 *
 */
public class PrintStageApplication {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.scan("io.mosip.registration.processor.stages.config", "io.mosip.registration.processor.status.config",
				"io.mosip.registration.processor.rest.client.config",
				"io.mosip.registration.processor.filesystem.ceph.adapter.impl.config",
				"io.mosip.registration.processor.packet.storage.config", "io.mosip.registration.processor.core.config",
				"io.mosip.registration.processor.core.kernel.beans");
		ctx.refresh();
		
		
	}
}
