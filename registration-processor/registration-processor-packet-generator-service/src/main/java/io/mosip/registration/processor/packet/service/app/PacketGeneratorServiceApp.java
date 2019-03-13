package io.mosip.registration.processor.packet.service.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;

import io.mosip.registration.processor.rest.client.config.RestConfigBean;

/**
 * The Class PacketGeneratorServiceApp.
 */
@SpringBootApplication
@ComponentScan(basePackages = {
		"io.mosip.registration.processor.packet.*,io.mosip.registration.processor.rest.client.*" }, excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, value = {
				RestConfigBean.class }))

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
