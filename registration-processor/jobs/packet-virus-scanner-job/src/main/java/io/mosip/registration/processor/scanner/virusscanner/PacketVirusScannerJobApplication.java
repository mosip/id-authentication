package io.mosip.registration.processor.scanner.virusscanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor.scanner.virusscanner",
		"io.mosip.registration.processor.status", "io.mosip.registration.processor.packet.manager",
		"io.mosip.kernel.virusscanner", "io.mosip.registration.processor.core",
		"io.mosip.registration.processor.filesystem.ceph.adapter.impl", "io.mosip.registration.processor.rest.client" })
public class PacketVirusScannerJobApplication {

	public static void main(String[] args) {
		SpringApplication.run(PacketVirusScannerJobApplication.class, args);
	}
}
