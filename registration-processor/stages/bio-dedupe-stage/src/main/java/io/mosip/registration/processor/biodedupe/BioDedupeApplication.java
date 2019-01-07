package io.mosip.registration.processor.biodedupe;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.mosip.registration.processor.biodedupe.stage.BioDedupeStage;

@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor.biodedupe",
		"io.mosip.registration.processor.status", "io.mosip.registration.processor.rest.client",
		"io.mosip.registration.processor.packet.storage", "io.mosip.registration.processor.core",
		"io.mosip.registration.processor.filesystem.ceph.adapter.impl",
		"io.mosip.registration.processor.bio.dedupe"})
public class BioDedupeApplication {
	/** The validatebean. */
	@Autowired
	private BioDedupeStage validatebean;

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(BioDedupeApplication.class, args);
	}

	/**
	 * Deploy verticle.
	 */
	@PostConstruct
	public void deployVerticle() {
		validatebean.deployVerticle();

	}

}
