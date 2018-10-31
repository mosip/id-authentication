package io.mosip.registration.processor.stages;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.mosip.registration.processor.stages.packet.validator.PacketValidatorStage;

@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor.stages",
		"io.mosip.registration.processor.status", "io.mosip.registration.processor.packet.manager",
		"io.mosip.registration.processor.packet.storage",
		"io.mosip.registration.processor.filesystem.ceph.adapter.impl" })

public class PacketValidatorApplication {

	@Autowired
	private PacketValidatorStage validatebean;

	public static void main(String[] args) {
		SpringApplication.run(PacketValidatorApplication.class, args);
	}

	@PostConstruct
	public void deployVerticle() {
		validatebean.deployVerticle();

	}
}
