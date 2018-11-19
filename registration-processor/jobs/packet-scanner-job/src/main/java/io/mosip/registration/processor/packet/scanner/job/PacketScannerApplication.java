package io.mosip.registration.processor.packet.scanner.job;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
	import org.springframework.context.annotation.PropertySource;

/**
 * The Class PacketScannerApplication.
 */
@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor.packet.scanner.job",
		"io.mosip.registration.processor.status", "io.mosip.registration.processor.packet.manager",
		"io.mosip.registration.processor.packet.receiver", "io.mosip.kernel.virusscanner",
		"io.mosip.registration.processor.auditmanager","io.mosip.registration.processor.filesystem.ceph.adapter.impl", "io.mosip.registration.processor.core" })
@PropertySource("classpath:virus-scanner-application.properties")
public class PacketScannerApplication {

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(PacketScannerApplication.class, args);
	}

}
