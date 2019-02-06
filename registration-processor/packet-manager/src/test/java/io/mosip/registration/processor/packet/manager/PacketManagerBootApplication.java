package io.mosip.registration.processor.packet.manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import io.mosip.registration.processor.packet.manager.config.PacketManagerConfig;


@SpringBootApplication
@ComponentScan(basePackages= "io.mosip.registration.processor.packet.manager.*",excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, value = PacketManagerConfig.class))
public class PacketManagerBootApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(PacketManagerBootApplication.class, args);
	}

}
