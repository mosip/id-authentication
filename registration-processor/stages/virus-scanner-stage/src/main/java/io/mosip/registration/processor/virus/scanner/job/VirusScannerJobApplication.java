package io.mosip.registration.processor.virus.scanner.job;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.mosip.registration.processor.virus.scanner.job.stage.VirusScannerStage;

@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor.virus.scanner.job",
		"io.mosip.registration.processor.core", "io.mosip.registration.processor.status",
		"io.mosip.registration.processor.packet.manager", "io.mosip.kernel.virusscanner.clamav",
		"io.mosip.registration.processor.filesystem.ceph.adapter.impl", "io.mosip.registration.processor.rest.client" })
public class VirusScannerJobApplication {

	@Autowired
	private VirusScannerStage virusScannerStage;

	public static void main(String[] args) {
		SpringApplication.run(VirusScannerJobApplication.class, args);
	}

	@PostConstruct
	public void deployVerticle() {
		virusScannerStage.deployVerticle();

	}
}
