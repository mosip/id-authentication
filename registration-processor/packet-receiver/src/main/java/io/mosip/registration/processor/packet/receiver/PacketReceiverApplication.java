package io.mosip.registration.processor.packet.receiver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The Class PacketReceiverApplication.
 */
@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor.packet.receiver",
		"io.mosip.registration.processor.status", "io.mosip.registration.processor.packet.manager",
		"io.mosip.registration.processor.rest.client" })

public class PacketReceiverApplication {

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(PacketReceiverApplication.class, args);
	}
}
