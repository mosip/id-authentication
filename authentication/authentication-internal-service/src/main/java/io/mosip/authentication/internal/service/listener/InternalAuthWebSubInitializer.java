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

/**
 * The Class InternalAuthWebSubInitializer.
 * @author Loganathan Sekar
 */
@Component
public class InternalAuthWebSubInitializer extends CacheUpdatingWebsubInitializer{
	
	/** The auth type status event subscriber. */
	@Autowired
	private AuthTypeStatusEventSubscriber authTypeStatusEventSubscriber;
	
	/** The id change event initializer. */
	@Autowired
	private IdChangeEventsInitializer idChangeEventInitializer;
	
	/** The partner CA cert event initializer. */
	@Autowired
	private PartnerCACertEventInitializer partnerCACertEventInitializer;
	
	/** The hotlist event initializer. */
	@Autowired
	private HotlistEventInitializer hotlistEventInitializer;
	
	/** The credential store status event publisher. */
	@Autowired 
	private CredentialStoreStatusEventPublisher credentialStoreStatusEventPublisher;
	
	/** The auth type status event publisher. */
	@Autowired
	private AuthTypeStatusEventPublisher authTypeStatusEventPublisher;
	
	/** The auth transaction status event publisher. */
	@Autowired
	private AuthTransactionStatusEventPublisher authTransactionStatusEventPublisher;
	
	/**
	 * Do init subscriptions.
	 */
	protected void doInitSubscriptions() {
		webSubHelper.initSubscriber(authTypeStatusEventSubscriber);
		webSubHelper.initSubscriber(idChangeEventInitializer);
		webSubHelper.initSubscriber(partnerCACertEventInitializer);
		webSubHelper.initSubscriber(hotlistEventInitializer);
	}


	/**
	 * Do register topics.
	 */
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
