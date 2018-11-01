package io.mosip.registration.processor.packet.decryptor.job;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
/**
 * The Class PacketDecryptorJobApplication.
 */
@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor.packet",
		"io.mosip.registration.processor.status", "io.mosip.registration.processor.core"})
@PropertySource({ "classpath:decryption-application.properties" })
@PropertySource({ "classpath:status-application.properties" })
public class PacketDecryptorJobApplication {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(PacketDecryptorJobApplication.class, args);
	}
}
