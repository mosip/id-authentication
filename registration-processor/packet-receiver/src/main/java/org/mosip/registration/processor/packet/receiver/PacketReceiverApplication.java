package org.mosip.registration.processor.packet.receiver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = { "org.mosip.registration.processor.packet.receiver",
		"org.mosip.registration.processor.status", "org.mosip.registration.processor.packet.manager" })
public class PacketReceiverApplication {
	public static void main(String[] args) {
		SpringApplication.run(PacketReceiverApplication.class, args);
	}
}
