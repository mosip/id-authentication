package io.mosip.registration.processor.scanner.ftp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor.scanner.ftp",
		"io.mosip.registration.processor.status", "io.mosip.registration.processor.packet.manager",
		"io.mosip.registration.processor.packet.receiver" })
@PropertySource({ "classpath:packet-manager-application.properties" })
@PropertySource({ "classpath:application.properties" })
@PropertySource({ "classpath:status-application.properties" })
@PropertySource({ "classpath:receiver-application.properties"})
public class PacketFtpScannerJobApplication {

	public static void main(String[] args) {
		SpringApplication.run(PacketFtpScannerJobApplication.class, args);
	}
}
