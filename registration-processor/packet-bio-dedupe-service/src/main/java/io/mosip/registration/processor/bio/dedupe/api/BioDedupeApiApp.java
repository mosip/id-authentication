package io.mosip.registration.processor.bio.dedupe.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;

import io.mosip.registration.processor.bio.dedupe.api.config.BioDedupeConfig;
import io.mosip.registration.processor.core.config.CoreConfigBean;
import io.mosip.registration.processor.core.dto.config.GlobalConfig;
import io.mosip.registration.processor.core.kernel.beans.KernelConfig;
import io.mosip.registration.processor.packet.storage.config.PacketStorageBeanConfig;
import io.mosip.registration.processor.rest.client.config.RestConfigBean;
import io.mosip.registration.processor.status.config.RegistrationStatusBeanConfig;


/**
 * The Class BioDedupeApiApp.
 */
@SpringBootApplication
@ComponentScan(basePackages= { "io.mosip.registration.processor.bio.dedupe.api.*",
		"io.mosip.registration.processor.filesystem.ceph.adapter.impl.*",
		"io.mosip.registration.processor.packet.storage.*",
		"io.mosip.registration.processor.rest.client.*",
		"io.mosip.registration.processor.bio.dedupe.*",
		"io.mosip.registration.processor.core.*"},
excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, value = {GlobalConfig.class,CoreConfigBean.class,RestConfigBean.class,PacketStorageBeanConfig.class}))

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
