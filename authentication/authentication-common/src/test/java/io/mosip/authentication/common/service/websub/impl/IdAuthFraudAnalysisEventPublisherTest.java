package io.mosip.authentication.common.service.websub.impl;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.websub.model.Event;
import io.mosip.kernel.core.websub.model.EventModel;

public class IdAuthFraudAnalysisEventPublisherTest extends AbstractEventInitializerTest<IdAuthFraudAnalysisEventPublisher>{

	@InjectMocks
	private ObjectMapper objectMapper;
	
	@Override
	protected IdAuthFraudAnalysisEventPublisher doCreateTestInstance() {
		IdAuthFraudAnalysisEventPublisher authTypeStatusEventSubscriber = new IdAuthFraudAnalysisEventPublisher();
		ReflectionTestUtils.setField(authTypeStatusEventSubscriber, "fraudAnalysisTopic" ,"fraudAnalysisTopic");
		ReflectionTestUtils.setField(authTypeStatusEventSubscriber, "objectMapper" ,objectMapper);
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
		IdAuthFraudAnalysisEventPublisher createTestInstance = createTestInstance();
		EventModel eventModel = new EventModel();
		eventModel.setEvent(new Event());
		Mockito.when(webSubHelper.createEventModel(Mockito.anyString())).thenReturn(eventModel);
		createTestInstance.publishEvent(Mockito.any());
	}
	

}
