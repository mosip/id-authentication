package io.mosip.registration.processor.packet.manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource({ "classpath:packet-manager-application.properties" })
public class PacketManagerApp {
	public static void main(String[] args) {
		SpringApplication.run(PacketManagerApp.class, args);
	}
}
