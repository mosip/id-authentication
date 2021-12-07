package io.mosip.authentication.common.service.websub.impl;

import org.springframework.test.util.ReflectionTestUtils;

public class PartnerCACertEventInitializerTest extends AbstractEventInitializerTest<PartnerCACertEventInitializer>{

	@Override
	protected PartnerCACertEventInitializer doCreateTestInstance() {
		PartnerCACertEventInitializer authTypeStatusEventSubscriber = new PartnerCACertEventInitializer();
		ReflectionTestUtils.setField(authTypeStatusEventSubscriber, "partnerCertCallbackURL" ,"partnerCertCallbackURL");
        ReflectionTestUtils.setField(authTypeStatusEventSubscriber, "partnerCertCallbackSecret" ,"partnerCertCallbackSecret");
        ReflectionTestUtils.setField(authTypeStatusEventSubscriber, "partnerCertEventTopic" ,"partnerCertEventTopic");
		return authTypeStatusEventSubscriber;
	}

}
