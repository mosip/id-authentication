package io.mosip.registration.processor.stages.app;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.mosip.registration.processor.stages.osivalidator.OSIValidatorStage;

/**
 * Hello world!
 *
 */
@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor.status",
		"io.mosip.registration.processor.filesystem.ceph.adapter.impl", "io.mosip.registration.processor.core",
		"io.mosip.registration.processor.rest.client", "io.mosip.registration.processor.stages.osivalidator" })
public class OSIValidatorApplication {

	/** The validatebean. */
	@Autowired
	private OSIValidatorStage validatebean;

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(OSIValidatorApplication.class, args);
	}

	/**
	 * Deploy verticle.
	 */
	@PostConstruct
	public void deployVerticle() {
		validatebean.deployVerticle();

	}

}
