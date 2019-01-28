package io.mosip.registration.processor.virus.scanner.job;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import io.mosip.registration.processor.virus.scanner.job.stage.VirusScannerStage;

public class VirusScannerJobApplication {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext configApplicationContext = new AnnotationConfigApplicationContext();
		configApplicationContext.scan("io.mosip.registration.processor.virus.scanner.job",
									  "io.mosip.registration.processor.packet.manager.config",
									  "io.mosip.registration.processor.status.config",
									  "io.mosip.registration.processor.core.kernel.beans");
		
		configApplicationContext.refresh();
		
		VirusScannerStage virusScannerStage = configApplicationContext.getBean(VirusScannerStage.class);
		virusScannerStage.deployVerticle();
	}

}
