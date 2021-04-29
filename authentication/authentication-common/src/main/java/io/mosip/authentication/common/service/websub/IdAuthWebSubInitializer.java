package io.mosip.authentication.common.service.websub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.websub.impl.PartnerServiceEventsInitializer;

/**
 * Websub Initializer for External facing IDA services such as Auth, EKYC and
 * OTP services.
 *
 * @author Loganathan Sekar
 * @author Manoj SP
 */
 
@Component
public final class IdAuthWebSubInitializer extends CacheUpdatingWebsubInitializer {
	
	/** The partner service events subscriber. */
	@Autowired
	private PartnerServiceEventsInitializer partnerServiceEventsInitializer;
	
	/**
	 * Do init subscriptions.
	 */
	@Override
	protected void doInitSubscriptions() {
		webSubHelper.initSubscriber(partnerServiceEventsInitializer, this::isCacheEnabled);
	}

	/**
	 * Do register topics.
	 */
	@Override
	protected void doRegisterTopics() {
		webSubHelper.initRegistrar(partnerServiceEventsInitializer, this::isCacheEnabled);
	}
	
}
