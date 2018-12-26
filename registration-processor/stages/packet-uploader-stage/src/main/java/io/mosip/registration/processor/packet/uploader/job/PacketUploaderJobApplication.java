package io.mosip.registration.processor.packet.uploader.job;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.mosip.registration.processor.packet.uploader.job.stage.PacketUploaderStage;

@SpringBootApplication(scanBasePackages = {"io.mosip.registration.processor.packet.uploader","io.mosip.registration.processor.core","io.mosip.registration.processor.packet.manager", "io.mosip.registration.processor.status",
		"io.mosip.registration.processor.filesystem.ceph.adapter.impl", "io.mosip.registration.processor.rest.client" })
public class PacketUploaderJobApplication {
	@Autowired
	private PacketUploaderStage packetUploaderStage;

	public static void main(String[] args) {
		SpringApplication.run(PacketUploaderJobApplication.class, args);
	}

	@PostConstruct
	public void deployVerticle() {
		packetUploaderStage.deployVerticle();
	}
}