package io.mosip.authentication.common.service.websub.impl;

import org.springframework.test.util.ReflectionTestUtils;

public class AuthTypeStatusEventSubscriberTest extends AbstractEventInitializerTest<AuthTypeStatusEventSubscriber>{

	@Override
	protected AuthTypeStatusEventSubscriber doCreateTestInstance() {
		AuthTypeStatusEventSubscriber authTypeStatusEventSubscriber = new AuthTypeStatusEventSubscriber();
		ReflectionTestUtils.setField(authTypeStatusEventSubscriber, "authTypeCallbackURL" ,"authTypeCallbackURL");
        ReflectionTestUtils.setField(authTypeStatusEventSubscriber, "autypeCallbackSecret" ,"autypeCallbackSecret");
        ReflectionTestUtils.setField(authTypeStatusEventSubscriber, "authPartherId" ,"authPartherId");
		return authTypeStatusEventSubscriber;
	}

}
