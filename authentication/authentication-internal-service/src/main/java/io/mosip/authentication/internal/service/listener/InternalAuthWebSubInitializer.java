package io.mosip.authentication.internal.service.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.websub.CacheUpdatingWebsubInitializer;
import io.mosip.authentication.common.service.websub.impl.AuthTypeStatusEventsSubscriber;
import io.mosip.authentication.common.service.websub.impl.HotlistEventSubscriber;
import io.mosip.authentication.common.service.websub.impl.IdChangeEventsSubscriber;
import io.mosip.authentication.common.service.websub.impl.PartnerCACertEventSubscriber;

@Component
public class InternalAuthWebSubInitializer extends CacheUpdatingWebsubInitializer{
	
	@Autowired
	private AuthTypeStatusEventsSubscriber authTypeStatusEventsSubscriber;
	
	@Autowired
	private IdChangeEventsSubscriber idChangeEventSubscriber;
	
	@Autowired
	private PartnerCACertEventSubscriber partnerCACertEventSubscriber;
	
	@Autowired
	private HotlistEventSubscriber hotlistEventSubscriber;
	
	
	protected void doInitSubscriptions() {
		webSubSubscriptionHelper.initSubscriber(authTypeStatusEventsSubscriber);
		webSubSubscriptionHelper.initSubscriber(idChangeEventSubscriber);
		webSubSubscriptionHelper.initSubscriber(partnerCACertEventSubscriber);
		webSubSubscriptionHelper.initSubscriber(hotlistEventSubscriber);
	}

}
