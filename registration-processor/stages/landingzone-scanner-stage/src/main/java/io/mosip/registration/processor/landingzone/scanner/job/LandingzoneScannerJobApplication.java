package io.mosip.registration.processor.landingzone.scanner.job;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

import io.mosip.registration.processor.landingzone.scanner.job.stage.LandingzoneScannerStage;

@SpringBootApplication(scanBasePackages = {"io.mosip.registration.processor.landingzone.scanner.job",
											"io.mosip.registration.processor.core",
											"io.mosip.registration.processor.status", 
											"io.mosip.registration.processor.packet.manager",
											"io.mosip.registration.processor.auditmanager"})
public class LandingzoneScannerJobApplication {

	@Autowired
	private LandingzoneScannerStage landingZoneScannerStage;

	public static void main(String[] args) {
		SpringApplication.run(LandingzoneScannerJobApplication.class, args);
	} 

	@PostConstruct
	public void deployVerticle() {
		landingZoneScannerStage.deployVerticle();

	}
}
