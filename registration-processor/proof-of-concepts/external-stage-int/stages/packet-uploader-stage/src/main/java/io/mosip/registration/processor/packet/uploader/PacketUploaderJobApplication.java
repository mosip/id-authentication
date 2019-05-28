package io.mosip.registration.processor.packet.uploader;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.mosip.registration.processor.packet.uploader.stage.PacketUploaderStage;

/**
 * The Class PacketUploaderJobApplication.
 */
@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor.packet.uploader",
		"io.mosip.registration.processor.core", "io.mosip.registration.processor.packet.manager",
		"io.mosip.registration.processor.status", "io.mosip.registration.processor.filesystem.ceph.adapter.impl",
		"io.mosip.registration.processor.rest.client" })
public class PacketUploaderJobApplication {

	/** The packet uploader stage. */
	@Autowired
	private PacketUploaderStage packetUploaderStage;

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(PacketUploaderJobApplication.class, args);
	}

	/**
	 * Deploy verticle.
	 */
	@PostConstruct
	public void deployVerticle() {
		packetUploaderStage.deployVerticle();
	}
}