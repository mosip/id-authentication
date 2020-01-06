package io.mosip.registration.processor.printing.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;

import io.mosip.registration.processor.core.kernel.beans.KernelConfig;
import io.mosip.registration.processor.packet.storage.config.PacketStorageBeanConfig;
import io.mosip.registration.processor.printing.config.PrintServiceBeanConfig;
import io.mosip.registration.processor.rest.client.config.RestConfigBean;
import io.mosip.registration.processor.status.config.RegistrationStatusBeanConfig;

/**
 * The Class PrintApiApp.
 * 
 * @author M1048358 Alok
 */
@SpringBootApplication
@ComponentScan(basePackages = { "io.mosip.registration.processor.core.*",
		"io.mosip.registration.processor.printing.api.config",
		"io.mosip.registration.processor.printing.api.controller", "io.mosip.registration.processor.printing.api.*",
		"io.mosip.registration.processor.print.service.*", "io.mosip.registration.processor.packet.storage.*",
		"io.mosip.registration.processor.message.sender.*", "io.mosip.registration.processor.status.*",
		"io.mosip.registration.processor.rest.client.*","io.mosip.registration.processor.packet.manager.*",
		"io.mosip.kernel.auth.*" }, excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, value = {
				RestConfigBean.class, PacketStorageBeanConfig.class, KernelConfig.class,
				PrintServiceBeanConfig.class, RegistrationStatusBeanConfig.class }))

public class PrintApiApplication {

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(PrintApiApplication.class, args);
	}
}
