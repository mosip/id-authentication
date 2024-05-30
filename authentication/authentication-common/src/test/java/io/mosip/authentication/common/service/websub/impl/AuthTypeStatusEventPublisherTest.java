package io.mosip.authentication.common.service.websub.impl;

import java.time.LocalDateTime;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.impl.idevent.AuthTransactionStatusEvent;
import io.mosip.authentication.common.service.impl.idevent.AuthTypeStatusUpdateAckEvent;

public class AuthTypeStatusEventPublisherTest extends AbstractEventInitializerTest<AuthTypeStatusEventPublisher>{

	@Mock
	private ObjectMapper mapper;
	
	@Override
	protected AuthTypeStatusEventPublisher doCreateTestInstance() {
		AuthTypeStatusEventPublisher authTypeStatusEventSubscriber = new AuthTypeStatusEventPublisher();
		ReflectionTestUtils.setField(authTypeStatusEventSubscriber, "authTypeStatusAcknlowedgeTopic" ,"authTypeStatusAcknlowedgeTopic");
		ReflectionTestUtils.setField(authTypeStatusEventSubscriber, "partnerId" ,"partnerId123");
		return authTypeStatusEventSubscriber;
	}
	
	
	@Test
	@Override
	public void testSubscribeForEventWithException() {
		// Nothing to test. Overriding super test that tests exception as this is only
		// publisher but not subscriber
	}
	
	@Test
	public void testPublishEvent() {
		AuthTypeStatusEventPublisher createTestInstance = createTestInstance();
		io.mosip.authentication.common.service.websub.dto.EventModel eventModel = new io.mosip.authentication.common.service.websub.dto.EventModel();
		eventModel.setEvent(Mockito.mock(AuthTypeStatusUpdateAckEvent.class));
		Mockito.when(webSubHelper.createEventModel(Mockito.anyString(), Mockito.any())).thenReturn(eventModel);
		createTestInstance.publishEvent("status", "requestid", LocalDateTime.now());
	}
	

}
