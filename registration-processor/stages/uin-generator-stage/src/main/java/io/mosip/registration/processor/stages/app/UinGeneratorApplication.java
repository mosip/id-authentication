package io.mosip.registration.processor.stages.app;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.mosip.registration.processor.stages.uigenerator.UinGeneratorStage;

@SpringBootApplication(scanBasePackages = { 
		"io.mosip.registration.processor.status", "io.mosip.registration.processor.filesystem.ceph.adapter.impl",
		"io.mosip.registration.processor.rest.client","io.mosip.registration.processor.packet.storage",
		"io.mosip.registration.processor.core","io.mosip.registration.processor.stages","io.mosip.registration.processor.message.sender"})
public class UinGeneratorApplication {

	/** The validatebean. */
	@Autowired
	private UinGeneratorStage validatebean;

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(UinGeneratorApplication.class, args);
	}

	/**
	 * Deploy verticle.
	 */
	@PostConstruct
	public void deployVerticle() {
		validatebean.deployVerticle();

	}
}