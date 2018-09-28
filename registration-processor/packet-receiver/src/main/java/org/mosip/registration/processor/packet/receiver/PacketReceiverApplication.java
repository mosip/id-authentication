package org.mosip.registration.processor.packet.receiver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication(scanBasePackages = { "org.mosip.registration.processor.packet.receiver",
		"org.mosip.registration.processor.status", "org.mosip.registration.processor.packet.manager" })
@PropertySource(value="receiver-application.properties")
@PropertySource(value="status-application.properties")
public class PacketReceiverApplication {
	public static void main(String[] args) {
		SpringApplication.run(PacketReceiverApplication.class, args);
	}
}
