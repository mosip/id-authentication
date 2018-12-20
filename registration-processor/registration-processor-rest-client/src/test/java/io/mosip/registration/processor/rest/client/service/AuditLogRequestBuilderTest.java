package io.mosip.registration.processor.rest.client.service;


import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.rest.client.audit.dto.AuditResponseDto;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class AuditLogRequestBuilderTest {

	@InjectMocks
	AuditLogRequestBuilder auditLogRequestBuilder;
	
	@Mock
	private RegistrationProcessorRestClientService<Object> registrationProcessorRestService;
	
	AuditResponseDto dto;
	
	@Test
	public void createAuditRequestBuilderTest() throws ApisResourceAccessException {
		dto=new AuditResponseDto();
		dto.setStatus(true);
		Mockito.when(registrationProcessorRestService.postApi(any(), any(), any(), any(), any())).thenReturn(dto);
		assertTrue(auditLogRequestBuilder.createAuditRequestBuilder("abcde", "200", "ADD", "ADD", "123456789").isStatus());
		
	}
}
