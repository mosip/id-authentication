package io.mosip.registration.processor.scanner.landingzone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor.scanner.landingzone",
		"io.mosip.registration.processor.status", "io.mosip.registration.processor.packet.manager",
		"io.mosip.registration.processor.core", "io.mosip.registration.processor.rest.client" })
public class PacketLandingzoneScannerJobApplication {

	public static void main(String[] args) {
		SpringApplication.run(PacketLandingzoneScannerJobApplication.class, args);
	}
}
