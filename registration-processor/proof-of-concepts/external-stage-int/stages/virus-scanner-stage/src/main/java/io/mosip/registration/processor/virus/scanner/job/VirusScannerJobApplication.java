package io.mosip.registration.processor.virus.scanner.job;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.mosip.registration.processor.virus.scanner.job.stage.VirusScannerStage;

/**
 * The Class VirusScannerJobApplication.
 */
@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor.virus.scanner.job",
		"io.mosip.registration.processor.core", "io.mosip.registration.processor.status",
		"io.mosip.registration.processor.packet.manager", "io.mosip.kernel.virusscanner.clamav",
		"io.mosip.registration.processor.rest.client" })
public class VirusScannerJobApplication {

	/** The virus scanner stage. */
	@Autowired
	private VirusScannerStage virusScannerStage;

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(VirusScannerJobApplication.class, args);
	}

	/**
	 * Deploy verticle.
	 */
	@PostConstruct
	public void deployVerticle() {
		virusScannerStage.deployVerticle();
	}
}
