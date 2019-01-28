package io.mosip.registration.processor.status.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.web.client.RestTemplateBuilder;

import io.mosip.registration.processor.status.service.TransactionService;
import io.mosip.kernel.dataaccess.hibernate.config.HibernateDaoConfig;
import io.mosip.kernel.dataaccess.hibernate.repository.impl.HibernateRepositoryImpl;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.rest.client.service.impl.RegistrationProcessorRestClientServiceImpl;
import io.mosip.registration.processor.rest.client.utils.RestApiClient;
import io.mosip.registration.processor.status.dao.RegistrationStatusDao;
import io.mosip.registration.processor.status.dao.SyncRegistrationDao;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.dto.SyncRegistrationDto;
import io.mosip.registration.processor.status.dto.SyncResponseDto;
import io.mosip.registration.processor.status.dto.TransactionDto;
import io.mosip.registration.processor.status.entity.BaseRegistrationEntity;
import io.mosip.registration.processor.status.entity.RegistrationStatusEntity;
import io.mosip.registration.processor.status.entity.SyncRegistrationEntity;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.mosip.registration.processor.status.service.SyncRegistrationService;
import io.mosip.registration.processor.status.service.impl.RegistrationStatusServiceImpl;
import io.mosip.registration.processor.status.service.impl.SyncRegistrationServiceImpl;
import io.mosip.registration.processor.status.service.impl.TransactionServiceImpl;

@Configuration
@PropertySource("classpath:bootstrap.properties")
@Import({ HibernateDaoConfig.class })
@EnableJpaRepositories(basePackages = "io.mosip.registration.processor", repositoryBaseClass = HibernateRepositoryImpl.class)
public class RegistrationStatusBeanConfig {

	@Bean
	public RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> getRegistrationStatusService() {
		return new RegistrationStatusServiceImpl();
	}

	@Bean
	public AuditLogRequestBuilder getAuditLogRequestBuilder() {
		return new AuditLogRequestBuilder();
	}

	@Bean
	public RegistrationProcessorRestClientService<Object> getRegistrationProcessorRestClientService() {
		return new RegistrationProcessorRestClientServiceImpl();
	}
	
	@Bean
	public SyncRegistrationDao getSyncRegistrationDao() {
		return new SyncRegistrationDao();
	}

	@Bean
	public RestTemplateBuilder getRestTemplateBuilder() {
		return new RestTemplateBuilder();
	}

	@Bean
	public RegistrationStatusDao getRegistrationStatusDao() {
		return new RegistrationStatusDao();	
	}

	@Bean
	public TransactionService<TransactionDto> getTransactionService() {
		return new TransactionServiceImpl();
	}
	
	@Bean
	public RestApiClient getRestApiClient() {
		return new RestApiClient();
	}

	@Bean
	public SyncRegistrationService<SyncResponseDto, SyncRegistrationDto> getSyncRegistrationService(){
		return new SyncRegistrationServiceImpl();
	}
//	@Bean
//	public RegistrationProcessorIdentity getRegProcessorIdentityJson() {
//		return new RegistrationProcessorIdentity();
//	}
	
	@Bean
	public InternalRegistrationStatusDto internalRegistrationStatusDto() {
		return new InternalRegistrationStatusDto();
	}
	
	@Bean
	public RegistrationStatusEntity registrationStatusEntity() {
		return new RegistrationStatusEntity();
	}
	
	@Bean
	public BaseRegistrationEntity baseRegistrationStatusEntity() {
		return new RegistrationStatusEntity();
	}
	
	@Bean
	public BaseRegistrationEntity baseSyncRegistrationEntity() {
		return new SyncRegistrationEntity();
	}
	
	
}
