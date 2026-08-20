package io.mosip.authentication.common.service.websub;

import java.util.Objects;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.websub.impl.AuthAnonymousEventPublisher;
import io.mosip.authentication.common.service.websub.impl.AuthTransactionStatusEventPublisher;
import io.mosip.authentication.common.service.websub.impl.AuthTypeStatusEventPublisher;
import io.mosip.authentication.common.service.websub.impl.AuthTypeStatusEventSubscriber;
import io.mosip.authentication.common.service.websub.impl.CredentialStoreStatusEventPublisher;
import io.mosip.authentication.common.service.websub.impl.HotlistEventInitializer;
import io.mosip.authentication.common.service.websub.impl.IdAuthFraudAnalysisEventPublisher;
import io.mosip.authentication.common.service.websub.impl.IdChangeEventsInitializer;
import io.mosip.authentication.common.service.websub.impl.MasterDataUpdateEventInitializer;
import io.mosip.authentication.common.service.websub.impl.PartnerCACertEventInitializer;
import io.mosip.authentication.common.service.websub.impl.PartnerServiceEventsInitializer;
import io.mosip.authentication.common.service.websub.impl.RemoveIdStatusEventPublisher;

/**
 * Websub Initializer for IDA services (external, OTP, and internal-service
 * webhook controllers merged into this application).
 *
 * @author Loganathan Sekar
 * @author Manoj SP
 */

@Component
public final class IdAuthWebSubInitializer extends CacheUpdatingWebsubInitializer {

	@Autowired
	private MasterDataUpdateEventInitializer masterDataUpdateEventInitializer;

	@Autowired(required=false)
	private IdAuthFraudAnalysisEventPublisher fraudEventPublisher;

	@Autowired
	private AuthTransactionStatusEventPublisher authTransactionStatusEventPublisher;

	@Autowired
	private AuthAnonymousEventPublisher authAnonymousEventPublisher;

	@Autowired
	private PartnerCACertEventInitializer partnerCACertEventInitializer;

	// Added for internal-service controller merge (phase 2): CredentialIssueanceCallbackController,
	// HotlistEventController, PartnerServiceCallbackController, InternalUpdateAuthTypeController
	// need their corresponding topics subscribed for the WebSub hub to actually call these endpoints.
	@Autowired
	private PartnerServiceEventsInitializer partnerServiceEventsInitializer;

	@Autowired
	private HotlistEventInitializer hotlistEventInitializer;

	@Autowired
	private IdChangeEventsInitializer idChangeEventsInitializer;

	@Autowired
	private AuthTypeStatusEventSubscriber authTypeStatusEventSubscriber;

	// Added for internal-service Spring Batch merge: these publishers were already @Import-ed in phase 2
	// (needed by CredentialStoreServiceImpl / IdChangeEventHandlerServiceImpl / UpdateAuthtypeStatusServiceImpl)
	// but were never actually registered as WebSub publishers - this was a gap left over from phase 2.
	@Autowired
	private CredentialStoreStatusEventPublisher credentialStoreStatusEventPublisher;

	@Autowired
	private AuthTypeStatusEventPublisher authTypeStatusEventPublisher;

	@Autowired
	private RemoveIdStatusEventPublisher removeIdStatusEventPublisher;

	/**
	 * Do init subscriptions.
	 */
	@Override
	protected int doInitSubscriptions() {
		webSubHelper.initSubscriber(masterDataUpdateEventInitializer, this::isCacheEnabled);
		webSubHelper.initSubscriber(partnerCACertEventInitializer);
		webSubHelper.initSubscriber(partnerServiceEventsInitializer);
		webSubHelper.initSubscriber(hotlistEventInitializer);
		webSubHelper.initSubscriber(idChangeEventsInitializer);
		webSubHelper.initSubscriber(authTypeStatusEventSubscriber);
		return HttpStatus.SC_OK;
	}

	/**
	 * Do register topics.
	 */
	@Override
	protected int doRegisterTopics() {
		//webSubHelper.initRegistrar(masterDataUpdateEventInitializer, this::isCacheEnabled);
		//webSubHelper.initRegistrar(partnerCACertEventInitializer);
		if(Objects.nonNull(fraudEventPublisher))
			webSubHelper.initRegistrar(fraudEventPublisher);
		webSubHelper.initRegistrar(authTransactionStatusEventPublisher);
		webSubHelper.initRegistrar(authAnonymousEventPublisher);
		webSubHelper.initRegistrar(credentialStoreStatusEventPublisher);
		webSubHelper.initRegistrar(authTypeStatusEventPublisher);
		webSubHelper.initRegistrar(removeIdStatusEventPublisher);
		return HttpStatus.SC_OK;
	}

}
