package io.mosip.authentication.common.service.websub.impl;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

public class PartnerServiceEventsInitializerTest extends AbstractEventInitializerTest<PartnerServiceEventsInitializer>{

	@BeforeEach
	public void before() {
	}
	
	@Override
	protected PartnerServiceEventsInitializer doCreateTestInstance() {
		PartnerServiceEventsInitializer authTypeStatusEventSubscriber = new PartnerServiceEventsInitializer();
		ReflectionTestUtils.setField(authTypeStatusEventSubscriber, "partnerServiceCallbackURL" ,"partnerServiceCallbackURL");
        ReflectionTestUtils.setField(authTypeStatusEventSubscriber, "partnerServiceCallbackSecret" ,"partnerServiceCallbackSecret");
		return authTypeStatusEventSubscriber;
	}

}
