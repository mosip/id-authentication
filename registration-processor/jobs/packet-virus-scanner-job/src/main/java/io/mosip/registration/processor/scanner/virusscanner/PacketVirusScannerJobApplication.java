package io.mosip.registration.processor.scanner.virusscanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor.scanner.virusscanner",
		"io.mosip.registration.processor.status", "io.mosip.registration.processor.packet.manager",
		 "io.mosip.kernel.virus.scanner"})
@PropertySource({ "classpath:packet-manager-application.properties" })
@PropertySource({ "classpath:application.properties" })
@PropertySource({ "classpath:status-application.properties" })
public class PacketVirusScannerJobApplication {

	public static void main(String[] args) {
		SpringApplication.run(PacketVirusScannerJobApplication.class, args);
	}
}
