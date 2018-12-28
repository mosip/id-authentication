package io.mosip.registration.processor.landingzone.scanner.job;
	
import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.mosip.registration.processor.landingzone.scanner.job.stage.LandingzoneScannerStage;

/**
 * The Class LandingzoneScannerJobApplication.
 */
@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor.landingzone.scanner.job",
		"io.mosip.registration.processor.core", "io.mosip.registration.processor.status",
		"io.mosip.registration.processor.packet.manager", "io.mosip.registration.processor.rest.client" })
public class LandingzoneScannerJobApplication {

	/** The landing zone scanner stage. */
	@Autowired
	private LandingzoneScannerStage landingZoneScannerStage;

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(LandingzoneScannerJobApplication.class, args);
	}

	/**
	 * Deploy verticle.
	 */
	@PostConstruct
	public void deployVerticle() {
		landingZoneScannerStage.deployVerticle();

	}
}
