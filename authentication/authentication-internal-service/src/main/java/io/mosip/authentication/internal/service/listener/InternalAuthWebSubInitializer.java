package io.mosip.authentication.internal.service.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.websub.CacheUpdatingWebsubInitializer;
import io.mosip.authentication.common.service.websub.impl.AuthTransactionStatusEventPublisher;
import io.mosip.authentication.common.service.websub.impl.AuthTypeStatusEventPublisher;
import io.mosip.authentication.common.service.websub.impl.AuthTypeStatusEventSubscriber;
import io.mosip.authentication.common.service.websub.impl.CredentialStoreStatusEventPublisher;
import io.mosip.authentication.common.service.websub.impl.HotlistEventInitializer;
import io.mosip.authentication.common.service.websub.impl.IdChangeEventsInitializer;
import io.mosip.authentication.common.service.websub.impl.PartnerCACertEventInitializer;

@Component
public class InternalAuthWebSubInitializer extends CacheUpdatingWebsubInitializer{
	
	@Autowired
	private AuthTypeStatusEventSubscriber authTypeStatusEventSubscriber;
	
	@Autowired
	private IdChangeEventsInitializer idChangeEventInitializer;
	
	@Autowired
	private PartnerCACertEventInitializer partnerCACertEventInitializer;
	
	@Autowired
	private HotlistEventInitializer hotlistEventInitializer;
	
	@Autowired 
	private CredentialStoreStatusEventPublisher credentialStoreStatusEventPublisher;
	
	@Autowired
	private AuthTypeStatusEventPublisher authTypeStatusEventPublisher;
	
	@Autowired
	private AuthTransactionStatusEventPublisher authTransactionStatusEventPublisher;
	
	protected void doInitSubscriptions() {
		webSubHelper.initSubscriber(authTypeStatusEventSubscriber);
		webSubHelper.initSubscriber(idChangeEventInitializer);
		webSubHelper.initSubscriber(partnerCACertEventInitializer);
		webSubHelper.initSubscriber(hotlistEventInitializer);
	}


	@Override
	protected void doRegisterTopics() {
		webSubHelper.initRegistrar(authTypeStatusEventSubscriber);
		webSubHelper.initRegistrar(idChangeEventInitializer);
		webSubHelper.initRegistrar(partnerCACertEventInitializer);
		webSubHelper.initRegistrar(hotlistEventInitializer);		
		
		webSubHelper.initRegistrar(credentialStoreStatusEventPublisher);		
		webSubHelper.initRegistrar(authTypeStatusEventPublisher);		
		webSubHelper.initRegistrar(authTransactionStatusEventPublisher);		
	}

}
