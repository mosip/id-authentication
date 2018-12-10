package io.mosip.registration.processor.ftp.scanner.job;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.mosip.registration.processor.ftp.scanner.job.stage.FtpScannerStage;

@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor.ftp.scanner.job",
											"io.mosip.registration.processor.core",
											"io.mosip.registration.processor.status",
											"io.mosip.registration.processor.packet.manager",
											"io.mosip.registration.processor.packet.receiver",
											"io.mosip.registration.processor.auditmanager"})
public class FtpScannerJobApplication {

	@Autowired
	private FtpScannerStage ftpScannerStage;

	public static void main(String[] args) {
		SpringApplication.run(FtpScannerJobApplication.class, args);
	}

	@PostConstruct
	public void deployVerticle() {
		ftpScannerStage.deployVerticle();

	}
}
