package io.mosip.registration.processor.packet.decrypter.job;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.mosip.registration.processor.packet.decrypter.job.stage.PacketDecrypterStage;
	
/**
 * The Class PacketDecrypterJobApplication.
 */
@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor.packet",
		"io.mosip.registration.processor.core", "io.mosip.registration.processor.status",
		"io.mosip.registration.processor.filesystem.ceph.adapter.impl", "io.mosip.registration.processor.rest.client" })
public class PacketDecrypterJobApplication {
	
	/** The packet decrypter stage. */
	@Autowired
	private PacketDecrypterStage packetDecrypterStage;

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(PacketDecrypterJobApplication.class, args);
	}

	/**
	 * Deploy verticle.
	 */
	@PostConstruct
	public void deployVerticle() {
		packetDecrypterStage.deployVerticle();
	}
}