package io.mosip.registration.processor.transaction.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;

import io.mosip.registration.processor.rest.client.config.RestConfigBean;
import io.mosip.registration.processor.status.config.RegistrationStatusBeanConfig;

@SpringBootApplication
@ComponentScan(basePackages= {"io.mosip.registration.processor.status.*","io.mosip.registration.processor.rest.client.*",
		"io.mosip.registration.processor.core.token.*", "io.mosip.registration.processor.core.config","io.mosip.registration.processor.transaction.*"},
excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, value = {RegistrationStatusBeanConfig.class,
		RestConfigBean.class}))
public class RegistrationProcessorRegistrationTransactionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RegistrationProcessorRegistrationTransactionServiceApplication.class, args);
	}

}
