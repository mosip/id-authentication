package io.mosip.registration.processor.packet.service.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "io.mosip.registration.processor.packet.*" })
public class PacketGeneratorServiceApp {

	public static void main(String[] args) {
		SpringApplication.run(PacketGeneratorServiceApp.class, args);
	}
}
