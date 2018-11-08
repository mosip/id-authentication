package io.mosip.registration.processor.scanner.landingzone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor.scanner.landingzone",
		"io.mosip.registration.processor.status","io.mosip.registration.processor.packet.manager"})
@PropertySource({ "classpath:packet-manager-application.properties" })
@PropertySource({ "classpath:application.properties" })
@PropertySource({ "classpath:status-application.properties" })
public class PacketLandingzoneScannerJobApplication {

	public static void main(String[] args) {
		SpringApplication.run(PacketLandingzoneScannerJobApplication.class, args);
	}
}
