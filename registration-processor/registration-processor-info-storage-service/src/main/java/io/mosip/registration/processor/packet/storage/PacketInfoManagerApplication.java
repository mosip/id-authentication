package io.mosip.registration.processor.packet.storage;

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
 * The Class PacketInfoManagerApplication.
 */
@SpringBootApplication
@ComponentScan(basePackages = { "io.mosip.registration.processor.packet.storage",
		"io.mosip.registration.processor.packet.manager", "io.mosip.registration.processor.core.config",
		"io.mosip.registration.processor.auditmanager",
		"io.mosip.registration.processor.rest.client","io.mosip.registration.processor.status.config" }, excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, value = {
				PacketStorageBeanConfig.class, RestConfigBean.class, KernelConfig.class }))

public class PacketInfoManagerApplication {

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(PacketInfoManagerApplication.class, args);
	}

}