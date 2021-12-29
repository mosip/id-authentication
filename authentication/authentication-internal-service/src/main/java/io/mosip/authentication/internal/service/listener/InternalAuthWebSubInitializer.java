package io.mosip.authentication.internal.service.listener;

import java.util.Objects;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.websub.CacheUpdatingWebsubInitializer;
import io.mosip.authentication.common.service.websub.impl.AuthTransactionStatusEventPublisher;
import io.mosip.authentication.common.service.websub.impl.AuthTypeStatusEventPublisher;
import io.mosip.authentication.common.service.websub.impl.AuthTypeStatusEventSubscriber;
import io.mosip.authentication.common.service.websub.impl.CredentialStoreStatusEventPublisher;
import io.mosip.authentication.common.service.websub.impl.HotlistEventInitializer;
import io.mosip.authentication.common.service.websub.impl.IdAuthFraudAnalysisEventPublisher;
import io.mosip.authentication.common.service.websub.impl.MasterDataUpdateEventInitializer;
import io.mosip.authentication.common.service.websub.impl.PartnerCACertEventInitializer;
import io.mosip.authentication.common.service.websub.impl.PartnerServiceEventsInitializer;

/**
 * The Class InternalAuthWebSubInitializer.
 * 
 * @author Loganathan Sekar
 */
@Component
public class InternalAuthWebSubInitializer extends CacheUpdatingWebsubInitializer {

	/** The auth type status event subscriber. */
	@Autowired
	private AuthTypeStatusEventSubscriber authTypeStatusEventSubscriber;

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

	/** The partner service events subscriber. */
	@Autowired
	private PartnerServiceEventsInitializer partnerServiceEventsInitializer;

	@Autowired
	private MasterDataUpdateEventInitializer masterDataUpdateEventInitializer;

	@Autowired(required=false)
	private IdAuthFraudAnalysisEventPublisher fraudEventPublisher;

	/**
	 * Do init subscriptions.
	 */
	protected int doInitSubscriptions() {
		webSubHelper.initSubscriber(authTypeStatusEventSubscriber);
		webSubHelper.initSubscriber(partnerCACertEventInitializer);
		webSubHelper.initSubscriber(hotlistEventInitializer);
		webSubHelper.initSubscriber(partnerServiceEventsInitializer);
		webSubHelper.initSubscriber(masterDataUpdateEventInitializer, this::isCacheEnabled);
		return HttpStatus.SC_OK;
	}

	/**
	 * Do register topics.
	 */
	@Override
	protected int doRegisterTopics() {
		//webSubHelper.initRegistrar(authTypeStatusEventSubscriber);
		//webSubHelper.initRegistrar(partnerCACertEventInitializer);
		//webSubHelper.initRegistrar(hotlistEventInitializer);
		//webSubHelper.initRegistrar(partnerServiceEventsInitializer);
		//webSubHelper.initRegistrar(masterDataUpdateEventInitializer, this::isCacheEnabled);
		webSubHelper.initRegistrar(credentialStoreStatusEventPublisher);
		webSubHelper.initRegistrar(authTypeStatusEventPublisher);
		webSubHelper.initRegistrar(authTransactionStatusEventPublisher);
		if(Objects.nonNull(fraudEventPublisher))
			webSubHelper.initRegistrar(fraudEventPublisher);
		return HttpStatus.SC_OK;
	}

}
