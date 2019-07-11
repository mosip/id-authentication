package io.mosip.registration.processor.rest.client.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.rest.client.service.impl.RegistrationProcessorRestClientServiceImpl;
import io.mosip.registration.processor.rest.client.utils.RestApiClient;

@Configuration
public class RestConfigBean {
	
	@Bean
	public RegistrationProcessorRestClientService<Object> getRegistrationProcessorRestClientService() {
		return new RegistrationProcessorRestClientServiceImpl();
	}

	@Bean
	public RestApiClient getRestApiClient() {
		return new RestApiClient();
	}
	
	@Bean 
	public AuditLogRequestBuilder getAuditLogRequestBuilder() {
		return new AuditLogRequestBuilder();
	}
	
	@Bean
	public RestTemplateBuilder getRestTemplateBuilder() {
		return new RestTemplateBuilder();
	}
}
