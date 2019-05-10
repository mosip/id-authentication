package io.mosip.registration.processor.bio.dedupe.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;

import io.mosip.registration.processor.core.config.CoreConfigBean;
import io.mosip.registration.processor.core.kernel.beans.KernelConfig;
import io.mosip.registration.processor.packet.storage.config.PacketStorageBeanConfig;
import io.mosip.registration.processor.rest.client.config.RestConfigBean;

/**
 * The Class BioDedupeApiApp.
 */
@SpringBootApplication
@ComponentScan(basePackages = { "io.mosip.registration.processor.stages.config",
		"io.mosip.registration.processor.demo.dedupe.config", "io.mosip.registration.processor.status.config",
		"io.mosip.registration.processor.packet.storage.config", "io.mosip.registration.processor.core.config",
		"io.mosip.registration.processor.core.kernel.beans"  }, excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, value = {
				RestConfigBean.class, CoreConfigBean.class, PacketStorageBeanConfig.class,
				KernelConfig.class }))

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
