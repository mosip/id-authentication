package io.mosip.registration.processor.scanner.ftp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor.scanner.ftp",
		"io.mosip.registration.processor.status", "io.mosip.registration.processor.packet.manager",
		"io.mosip.registration.processor.packet.receiver", "io.mosip.registration.processor.core","io.mosip.registration.processor.rest.client" })
public class PacketFtpScannerJobApplication {

	public static void main(String[] args) {
		SpringApplication.run(PacketFtpScannerJobApplication.class, args);
	}
}
