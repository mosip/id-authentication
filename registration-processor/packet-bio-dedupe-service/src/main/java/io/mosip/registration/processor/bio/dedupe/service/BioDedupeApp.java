package io.mosip.registration.processor.bio.dedupe.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor.bio.dedupe",
		"io.mosip.registration.processor.rest.client", "io.mosip.registration.processor.packet.storage",
		"io.mosip.registration.processor.core", "io.mosip.registration.processor.filesystem.ceph.adapter.impl" })
public class BioDedupeApp {

	public static void main(String[] args) {
		SpringApplication.run(BioDedupeApp.class, args);
	}
}
