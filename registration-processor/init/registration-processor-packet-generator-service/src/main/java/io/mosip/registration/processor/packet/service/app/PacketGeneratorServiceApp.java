package io.mosip.registration.processor.packet.service.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;

import io.mosip.registration.processor.packet.storage.config.PacketStorageBeanConfig;
import io.mosip.registration.processor.rest.client.config.RestConfigBean;
import io.mosip.registration.processor.status.config.RegistrationStatusBeanConfig;

/**
 * The Class PacketGeneratorServiceApp.
 * 
 * @author Sowmya
 */
@SpringBootApplication
@ComponentScan(basePackages = {
		"io.mosip.registration.processor.packet.*,io.mosip.registration.processor.rest.client.*,io.mosip.registration.processor.core.*","io.mosip.kernel.auth.*"}, excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, value = {
				RestConfigBean.class,PacketStorageBeanConfig.class,RegistrationStatusBeanConfig.class }))

public class PacketGeneratorServiceApp {

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(PacketGeneratorServiceApp.class, args);
	}
}
