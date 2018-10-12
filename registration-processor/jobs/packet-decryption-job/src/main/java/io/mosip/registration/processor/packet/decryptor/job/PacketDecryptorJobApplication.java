package io.mosip.registration.processor.packet.decryptor.job;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor.packet.decryptor.job",
		"io.mosip.registration.processor.status" })
@PropertySource({ "classpath:decryption-application.properties" })
@PropertySource({ "classpath:status-application.properties" })
public class PacketDecryptorJobApplication {

	public static void main(String[] args) {
		SpringApplication.run(PacketDecryptorJobApplication.class, args);
	}
}
