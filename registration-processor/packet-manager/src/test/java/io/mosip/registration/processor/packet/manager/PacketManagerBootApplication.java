package io.mosip.registration.processor.packet.manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages="io.mosip.registration.processor.*")
public class PacketManagerBootApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(PacketManagerBootApplication.class, args);
	}

}
