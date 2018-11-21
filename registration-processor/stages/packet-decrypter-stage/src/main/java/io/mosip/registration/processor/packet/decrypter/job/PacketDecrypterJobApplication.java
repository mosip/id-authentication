package io.mosip.registration.processor.packet.decrypter.job;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

import io.mosip.registration.processor.packet.decrypter.job.stage.PacketDecrypterStage;

@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor.packet",
											"io.mosip.registration.processor.core",
											"io.mosip.registration.processor.status",
											 "io.mosip.registration.processor.filesystem.ceph.adapter.impl",
												"io.mosip.registration.processor.auditmanager" })
public class PacketDecrypterJobApplication {
	@Autowired
	private PacketDecrypterStage packetDecrypterStage;

	public static void main(String[] args) {
		SpringApplication.run(PacketDecrypterJobApplication.class, args);
	}

	@PostConstruct
	public void deployVerticle() {
		packetDecrypterStage.deployVerticle();
	}
}