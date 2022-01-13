package io.mosip.authentication.common.service.websub;

import java.util.Objects;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.websub.impl.AuthAnonymousEventPublisher;
import io.mosip.authentication.common.service.websub.impl.AuthTransactionStatusEventPublisher;
import io.mosip.authentication.common.service.websub.impl.IdAuthFraudAnalysisEventPublisher;
import io.mosip.authentication.common.service.websub.impl.MasterDataUpdateEventInitializer;
import io.mosip.authentication.common.service.websub.impl.PartnerCACertEventInitializer;

/**
 * Websub Initializer for External facing IDA services such as Auth, EKYC and
 * OTP services.
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

	/**
	 * Do init subscriptions.
	 */
	@Override
	protected int doInitSubscriptions() {
		webSubHelper.initSubscriber(masterDataUpdateEventInitializer, this::isCacheEnabled);
		webSubHelper.initSubscriber(partnerCACertEventInitializer);
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
		return HttpStatus.SC_OK;
	}

}
