package io.mosip.registration.processor.packet.receiver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor.packet.receiver",
		"io.mosip.registration.processor.status", "io.mosip.registration.processor.packet.manager","io.mosip.registration.processor.core" })
@PropertySource({ "classpath:receiver-application.properties"})
@PropertySource({ "classpath:status-application.properties" })
public class PacketReceiverApplication {
	public static void main(String[] args) {
		SpringApplication.run(PacketReceiverApplication.class, args);
	}
}
