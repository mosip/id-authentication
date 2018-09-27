package org.mosip.registration.processor.packet.decryptor.job;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
@SpringBootApplication(scanBasePackages = { "org.mosip.registration.processor.packet.decryptor.job",
		"org.mosip.registration.processor.status" })
@PropertySource({ "classpath:application.properties" })
@PropertySource({ "classpath:status-application.properties" })
public class PacketDecryptorJobApplication {

	public static void main(String[] args) {
		SpringApplication.run(PacketDecryptorJobApplication.class, args);
	}
}
