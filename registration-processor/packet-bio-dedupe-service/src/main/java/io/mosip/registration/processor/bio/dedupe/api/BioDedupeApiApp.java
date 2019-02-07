package io.mosip.registration.processor.bio.dedupe.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;

import io.mosip.registration.processor.bio.dedupe.api.config.BioDedupeConfig;
import io.mosip.registration.processor.core.config.CoreConfigBean;
import io.mosip.registration.processor.core.kernel.beans.KernelConfig;
import io.mosip.registration.processor.packet.storage.config.PacketStorageBeanConfig;
import io.mosip.registration.processor.rest.client.config.RestConfigBean;
import io.mosip.registration.processor.status.config.RegistrationStatusBeanConfig;

/**
 * The Class BioDedupeApiApp.
 */
@SpringBootApplication
@ComponentScan(basePackages= "io.mosip.registration.processor.*",
excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, value = {BioDedupeConfig.class,
		RestConfigBean.class, RegistrationStatusBeanConfig.class, PacketStorageBeanConfig.class,
		KernelConfig.class, PacketStorageBeanConfig.class, CoreConfigBean.class}))

public class BioDedupeApiApp{

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
