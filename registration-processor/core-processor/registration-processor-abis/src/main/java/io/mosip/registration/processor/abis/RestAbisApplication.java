package io.mosip.registration.processor.abis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import io.mosip.registration.processor.abis.config.RegistrationAbisConfig;
import io.mosip.registration.processor.abis.messagequeue.AbisMessageQueueImpl;
import io.mosip.registration.processor.core.exception.RegistrationProcessorCheckedException;
import io.mosip.registration.processor.packet.manager.config.PacketManagerConfig;
import io.mosip.registration.processor.packet.storage.config.PacketStorageBeanConfig;
import io.mosip.registration.processor.rest.client.config.RestConfigBean;
import io.mosip.registration.processor.status.config.RegistrationStatusBeanConfig;

@Configuration
@EnableAutoConfiguration
@SpringBootApplication
@ComponentScan(basePackages = { "io.mosip.registration.processor.*",
		"io.mosip.kernel.auth.*" }, excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, value = {
				PacketManagerConfig.class, RestConfigBean.class, PacketManagerConfig.class,
				RegistrationStatusBeanConfig.class, PacketStorageBeanConfig.class, RegistrationAbisConfig.class }))
public class RestAbisApplication{
	public static void main(String[] args) throws RegistrationProcessorCheckedException {
		ConfigurableApplicationContext configurableApplcnConetxt = SpringApplication.run(RestAbisApplication.class, args);
		configurableApplcnConetxt.getBean(AbisMessageQueueImpl.class).runAbisQueue();
	}
}
