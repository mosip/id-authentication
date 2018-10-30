package io.mosip.registration.processor.packet.decryptor.job;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor.packet",
		"io.mosip.registration.processor.status" })

public class PacketDecryptorJobApplication {

	public static void main(String[] args) {
		SpringApplication.run(PacketDecryptorJobApplication.class, args);
	}
}
