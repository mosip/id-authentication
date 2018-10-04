package io.mosip.registration.processor.packet.scanner.job;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor.packet.scanner.job",
		"io.mosip.registration.processor.status", "io.mosip.registration.processor.packet.manager",
		"io.mosip.registration.processor.packet.receiver", "io.mosip.kernel.virus.scanner"})
@PropertySource({ "classpath:packet-manager-application.properties" })
@PropertySource({ "classpath:application.properties" })
@PropertySource({ "classpath:status-application.properties" })
@PropertySource({ "classpath:receiver-application.properties"})
public class PacketScannerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PacketScannerApplication.class, args);
	}

}
