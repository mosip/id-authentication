package io.mosip.registration.processor.bio.dedupe.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;

import io.mosip.registration.processor.packet.storage.config.PacketStorageBeanConfig;
import io.mosip.registration.processor.rest.client.config.RestConfigBean;
import io.mosip.registration.processor.status.config.RegistrationStatusBeanConfig;

/**
 * The Class BioDedupeApiApp.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"io.mosip.registration.processor.*",	"io.mosip.kernel.auth.*" },
 excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, value = {
		 RegistrationStatusBeanConfig.class,RestConfigBean.class ,PacketStorageBeanConfig.class}))

public class BioDedupeApiApp {

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(BioDedupeApiApp.class, args);
	}
}
