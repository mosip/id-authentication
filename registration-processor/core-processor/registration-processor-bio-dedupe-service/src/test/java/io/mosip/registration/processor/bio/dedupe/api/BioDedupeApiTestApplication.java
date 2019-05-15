package io.mosip.registration.processor.bio.dedupe.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;

import io.mosip.registration.processor.bio.dedupe.api.config.TestSecurityConfig;
import io.mosip.registration.processor.core.config.CoreConfigBean;
import io.mosip.registration.processor.core.kernel.beans.KernelConfig;
import io.mosip.registration.processor.packet.storage.config.PacketStorageBeanConfig;
import io.mosip.registration.processor.rest.client.config.RestConfigBean;

/**
 * The Class BioDedupeApiApp.
 */
@SpringBootApplication
@ComponentScan(basePackages = {
		"io.mosip.registration.processor.bio.dedupe.api.*" }, excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, value = {
				RestConfigBean.class, CoreConfigBean.class, PacketStorageBeanConfig.class, KernelConfig.class }))
@Import(TestSecurityConfig.class)
public class BioDedupeApiTestApplication {

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(BioDedupeApiTestApplication.class, args);
	}
}
