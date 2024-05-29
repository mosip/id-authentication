package io.mosip.authentication.common.service.websub.impl;

import java.time.LocalDateTime;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.impl.idevent.AuthTransactionStatusEvent;

public class CredentialStoreStatusEventPublisherTest extends AbstractEventInitializerTest<CredentialStoreStatusEventPublisher>{

	@InjectMocks
	private ObjectMapper mapper;
	
	@Override
	protected CredentialStoreStatusEventPublisher doCreateTestInstance() {
		CredentialStoreStatusEventPublisher authTypeStatusEventSubscriber = new CredentialStoreStatusEventPublisher();
		ReflectionTestUtils.setField(authTypeStatusEventSubscriber, "credentialStatusUpdateTopic" ,"credentialStatusUpdateTopic");
		return authTypeStatusEventSubscriber;
	}
	
	
	@Test
	@Override
	public void testSubscribeForEventWithException() {
		// Nothing to test. Overriding super test that tests exception as this is only
		// publisher but not subscriber
	}
	
	@Test (expected = Exception.class)
	public void testPublishEvent() {
		CredentialStoreStatusEventPublisher createTestInstance = createTestInstance();
		io.mosip.authentication.common.service.websub.dto.EventModel eventModel = new io.mosip.authentication.common.service.websub.dto.EventModel();
		eventModel.setEvent(Mockito.mock(AuthTransactionStatusEvent.class));
		Mockito.when(webSubHelper.createEventModel(Mockito.anyString(), Mockito.any())).thenReturn(eventModel);
		createTestInstance.publishEvent("status", "requestid", LocalDateTime.now());
	}
	

}
