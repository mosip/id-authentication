package io.mosip.authentication.internal.service.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.websub.CacheUpdatingWebsubInitializer;
import io.mosip.authentication.common.service.websub.impl.AuthTypeStatusEventsInitializer;
import io.mosip.authentication.common.service.websub.impl.CredentialStoreStatusEventManager;
import io.mosip.authentication.common.service.websub.impl.HotlistEventInitializer;
import io.mosip.authentication.common.service.websub.impl.IdChangeEventsInitializer;
import io.mosip.authentication.common.service.websub.impl.PartnerCACertEventInitializer;

@Component
public class InternalAuthWebSubInitializer extends CacheUpdatingWebsubInitializer{
	
	@Autowired
	private AuthTypeStatusEventsInitializer authTypeStatusEventsInitializer;
	
	@Autowired
	private IdChangeEventsInitializer idChangeEventInitializer;
	
	@Autowired
	private PartnerCACertEventInitializer partnerCACertEventInitializer;
	
	@Autowired
	private HotlistEventInitializer hotlistEventInitializer;
	
	@Autowired 
	private CredentialStoreStatusEventManager credentialStoreStatusEventManager;
	
	protected void doInitSubscriptions() {
		webSubHelper.initSubscriber(authTypeStatusEventsInitializer);
		webSubHelper.initSubscriber(idChangeEventInitializer);
		webSubHelper.initSubscriber(partnerCACertEventInitializer);
		webSubHelper.initSubscriber(hotlistEventInitializer);
	}


	@Override
	protected void doRegisterTopics() {
		webSubHelper.initRegistrar(authTypeStatusEventsInitializer);
		webSubHelper.initRegistrar(idChangeEventInitializer);
		webSubHelper.initRegistrar(partnerCACertEventInitializer);
		webSubHelper.initRegistrar(hotlistEventInitializer);		
		
		webSubHelper.initRegistrar(credentialStoreStatusEventManager);		
	}

}
