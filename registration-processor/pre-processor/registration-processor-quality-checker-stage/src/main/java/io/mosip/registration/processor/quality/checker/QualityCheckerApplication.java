package io.mosip.registration.processor.quality.checker;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import io.mosip.registration.processor.quality.checker.stage.QualityCheckerStage;

/**
 * The Class QualityCheckerApplication.
 * 
 * @author M1048358 Alok Ranjan
 */
public class QualityCheckerApplication {

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.scan("io.mosip.registration.processor.quality.checker.config",
				"io.mosip.registration.processor.status.config", "io.mosip.registration.processor.rest.client.config",
				"io.mosip.registration.processor.packet.storage.config", "io.mosip.registration.processor.core.config",
				"io.mosip.registration.processor.core.kernel.beans",
				"io.mosip.registration.processor.packet.manager.config");
		ctx.refresh();
		QualityCheckerStage qualityCheckerStage = ctx.getBean(QualityCheckerStage.class);
		qualityCheckerStage.deployVerticle();
	}
}
