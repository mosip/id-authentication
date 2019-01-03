package io.mosip.registration.processor.rest.client.service;


import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
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
	private Logger fooLogger;
	
	private ListAppender<ILoggingEvent> listAppender;	
	
	@Test
	public void createAuditRequestBuilderTest() throws ApisResourceAccessException {
		dto=new AuditResponseDto();
		dto.setStatus(true);
		Mockito.when(registrationProcessorRestService.postApi(any(), any(), any(), any(), any())).thenReturn(dto);
		assertTrue(auditLogRequestBuilder.createAuditRequestBuilder("abcde", "200", "ADD", "ADD", "123456789").isStatus());
		
	}
	
	@Test
	public void createAuditRequestBuilderFailureTest() throws ApisResourceAccessException {
		 fooLogger = (Logger) LoggerFactory.getLogger(AuditLogRequestBuilder.class);
	     listAppender = new ListAppender<>();
	     listAppender.start();
	     fooLogger.addAppender(listAppender);
		ApisResourceAccessException exp = new ApisResourceAccessException("errorMessage");
		Mockito.when(registrationProcessorRestService.postApi(any(), any(), any(), any(), any())).thenThrow(exp);
		
		auditLogRequestBuilder.createAuditRequestBuilder("abcde", "200", "ADD", "ADD", "123456789");
		Assertions.assertThat(listAppender.list)
        .extracting( ILoggingEvent::getLevel, ILoggingEvent::getFormattedMessage)
		.containsExactly(Tuple.tuple( Level.ERROR, "RPR-RCT-001 --> errorMessage")); 
	}
}
