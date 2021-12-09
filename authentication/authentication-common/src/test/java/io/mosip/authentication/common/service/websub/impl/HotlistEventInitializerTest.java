package io.mosip.authentication.common.service.websub.impl;

import org.springframework.test.util.ReflectionTestUtils;

public class HotlistEventInitializerTest extends AbstractEventInitializerTest<HotlistEventInitializer>{

	@Override
	protected HotlistEventInitializer doCreateTestInstance() {
		HotlistEventInitializer authTypeStatusEventSubscriber = new HotlistEventInitializer();
		ReflectionTestUtils.setField(authTypeStatusEventSubscriber, "hotlistCallbackURL" ,"hotlistCallbackURL");
        ReflectionTestUtils.setField(authTypeStatusEventSubscriber, "hotlistCallbackSecret" ,"hotlistCallbackSecret");
        ReflectionTestUtils.setField(authTypeStatusEventSubscriber, "hotlistEventTopic" ,"hotlistEventTopic");
		return authTypeStatusEventSubscriber;
	}

}
