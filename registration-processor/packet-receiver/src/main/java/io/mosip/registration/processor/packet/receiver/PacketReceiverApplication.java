package io.mosip.registration.processor.packet.receiver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

/**
 * The Class PacketReceiverApplication.
 */
@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor.packet.receiver",
		"io.mosip.registration.processor.auditmanager","io.mosip.registration.processor.status", "io.mosip.registration.processor.packet.manager" })
public class PacketReceiverApplication {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(PacketReceiverApplication.class, args);
	}
}
