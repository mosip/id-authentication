package io.mosip.authentication.common.service.websub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.websub.impl.IdAuthFraudAnalysisEventPublisher;
import io.mosip.authentication.common.service.websub.impl.MasterDataUpdateEventInitializer;

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
	
	@Autowired
	private IdAuthFraudAnalysisEventPublisher fraudEventPublisher;

	/**
	 * Do init subscriptions.
	 */
	@Override
	protected void doInitSubscriptions() {
		webSubHelper.initSubscriber(masterDataUpdateEventInitializer, this::isCacheEnabled);
	}

	/**
	 * Do register topics.
	 */
	@Override
	protected void doRegisterTopics() {
		webSubHelper.initRegistrar(masterDataUpdateEventInitializer, this::isCacheEnabled);
		webSubHelper.initRegistrar(fraudEventPublisher);
	}

}
