package io.mosip.authentication.common.service.websub.impl;

import org.springframework.test.util.ReflectionTestUtils;

public class MasterDataUpdateEventInitializerTest extends AbstractEventInitializerTest<MasterDataUpdateEventInitializer>{

	@Override
	protected MasterDataUpdateEventInitializer doCreateTestInstance() {
		MasterDataUpdateEventInitializer authTypeStatusEventSubscriber = new MasterDataUpdateEventInitializer();
		ReflectionTestUtils.setField(authTypeStatusEventSubscriber, "masterdataTemplatesCallbackURL" ,"masterdataTemplatesCallbackURL");
        ReflectionTestUtils.setField(authTypeStatusEventSubscriber, "masterdataTemplatesCallbackSecret" ,"masterdataTemplatesCallbackSecret");
        ReflectionTestUtils.setField(authTypeStatusEventSubscriber, "masterdataTemplatesEventTopic" ,"masterdataTemplatesEventTopic");
        ReflectionTestUtils.setField(authTypeStatusEventSubscriber, "masterdataTitlesCallbackURL" ,"masterdataTitlesCallbackURL");
        ReflectionTestUtils.setField(authTypeStatusEventSubscriber, "masterdataTitlesCallbackSecret" ,"masterdataTitlesCallbackSecret");
        ReflectionTestUtils.setField(authTypeStatusEventSubscriber, "masterdataTitlesEventTopic" ,"masterdataTitlesEventTopic");
		return authTypeStatusEventSubscriber;
	}

}
