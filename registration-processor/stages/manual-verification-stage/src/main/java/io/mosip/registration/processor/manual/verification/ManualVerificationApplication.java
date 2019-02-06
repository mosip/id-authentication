package io.mosip.registration.processor.manual.verification;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import io.mosip.registration.processor.manual.verification.stage.ManualVerificationStage;

/**
 * ManualAdjudicationApplication Main class	.
 *
 * @author Pranav Kumar
 * @since 0.0.1
 */
public class ManualVerificationApplication {


	/**
	 * Main method to instantiate the spring boot application.
	 *
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		AnnotationConfigApplicationContext configApplicationContext = new AnnotationConfigApplicationContext();
		configApplicationContext.scan(
				  "io.mosip.registration.processor.manual.verification.config",
				  "io.mosip.registration.processor.packet.receiver.config",
				  "io.mosip.registration.processor.packet.manager.config",
				  "io.mosip.registration.processor.status.config",
				  "io.mosip.registration.processor.rest.client.config",
				  "io.mosip.registration.processor.filesystem.ceph.adapter.impl.config",
				  "io.mosip.registration.processor.core.kernel.beans");
		configApplicationContext.refresh();
		ManualVerificationStage manualVerificationStage = configApplicationContext.getBean(ManualVerificationStage.class);
		manualVerificationStage.deployStage();
	}
}