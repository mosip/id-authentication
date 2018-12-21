package io.mosip.registration.processor.packet.receiver;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.mosip.registration.processor.packet.receiver.stage.PacketReceiverStage;

/**
 * The Class PacketReceiverApplication.
 */
@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor.packet.receiver",
		"io.mosip.registration.processor.status", "io.mosip.registration.processor.packet.manager",
		"io.mosip.registration.processor.rest.client" })

public class PacketReceiverApplication {

	@Autowired
	PacketReceiverStage packetReceiverStage;

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(PacketReceiverApplication.class, args);
	}

	@PostConstruct
	public void deployManualVerificationStage() {
		packetReceiverStage.deployStage();
	}

}
