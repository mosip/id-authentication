package io.mosip.authentication.common.service.websub.impl;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.impl.idevent.AnonymousAuthenticationProfile;
import io.mosip.kernel.core.websub.model.Event;
import io.mosip.kernel.core.websub.model.EventModel;

import static org.mockito.Mockito.when;

public class AuthAnonymousEventPublisherTest extends AbstractEventInitializerTest<AuthAnonymousEventPublisher>{

	@InjectMocks
	private ObjectMapper mapper;
	
	@Override
	protected AuthAnonymousEventPublisher doCreateTestInstance() {
		AuthAnonymousEventPublisher authTypeStatusEventSubscriber = new AuthAnonymousEventPublisher();
		ReflectionTestUtils.setField(authTypeStatusEventSubscriber, "authAnanymousProfileTopic" ,"authAnanymousProfileTopic");
        ReflectionTestUtils.setField(authTypeStatusEventSubscriber, "objectMapper" , mapper);
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
		AuthAnonymousEventPublisher createTestInstance = createTestInstance();
		EventModel eventModel = new EventModel();
		eventModel.setEvent(new Event());
		when(webSubHelper.createEventModel("any_string_value")).thenReturn(eventModel);
		createTestInstance.publishEvent(Mockito.mock(AnonymousAuthenticationProfile.class));
	}
	

}
