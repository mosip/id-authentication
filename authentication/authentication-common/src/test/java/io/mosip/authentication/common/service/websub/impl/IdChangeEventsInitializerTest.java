package io.mosip.authentication.common.service.websub.impl;

import org.springframework.test.util.ReflectionTestUtils;

public class IdChangeEventsInitializerTest extends AbstractEventInitializerTest<IdChangeEventsInitializer>{

	@Override
	protected IdChangeEventsInitializer doCreateTestInstance() {
		IdChangeEventsInitializer authTypeStatusEventSubscriber = new IdChangeEventsInitializer();
		ReflectionTestUtils.setField(authTypeStatusEventSubscriber, "credentialIssueCallbackURL" ,"credentialIssueCallbackURL");
        ReflectionTestUtils.setField(authTypeStatusEventSubscriber, "credIssueCallbacksecret" ,"credIssueCallbacksecret");
        ReflectionTestUtils.setField(authTypeStatusEventSubscriber, "authPartherId" ,"authPartherId");
		return authTypeStatusEventSubscriber;
	}

}
