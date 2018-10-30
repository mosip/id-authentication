package io.mosip.registration.processor.packet.scanner.job;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor.packet.scanner.job",
		"io.mosip.registration.processor.status", "io.mosip.registration.processor.packet.manager",
		"io.mosip.registration.processor.packet.receiver", "io.mosip.kernel.virus.scanner" })

public class PacketScannerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PacketScannerApplication.class, args);
	}

}
