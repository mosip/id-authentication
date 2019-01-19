package io.mosip.registration.processor.packet.storage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The Class PacketInfoManagerApplication.
 */
@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor.packet.storage",
		"io.mosip.registration.processor.packet.manager", "io.mosip.registration.processor.core",
		"io.mosip.registration.processor.auditmanager", "io.mosip.registration.processor.filesystem.ceph.adapter.impl",
		"io.mosip.registration.processor.rest.client" })

public class PacketInfoManagerApplication {

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(PacketInfoManagerApplication.class, args);
	}

}