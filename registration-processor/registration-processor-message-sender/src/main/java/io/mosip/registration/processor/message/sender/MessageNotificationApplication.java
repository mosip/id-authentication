package io.mosip.registration.processor.message.sender;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Alok Ranjan
 * @author Shuchita
 * @author Ayush Keer
 */
@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor.message.sender",
		"io.mosip.registration.processor.core", "io.mosip.registration.processor.packet.storage",
		"io.mosip.registration.processor.rest.client", "io.mosip.registration.processor.filesystem.ceph.adapter.impl" })
public class MessageNotificationApplication {

	/**
	 * Main method to instantiate the spring boot application
	 *
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(MessageNotificationApplication.class, args);
	}

}
