package io.mosip.authentication.common.service.websub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

	/**
	 * Do init subscriptions.
	 */
	@Override
	protected void doInitSubscriptions() {
		masterDataUpdateEventInitializer.subscribe(this::isCacheEnabled);
	}

	/**
	 * Do register topics.
	 */
	@Override
	protected void doRegisterTopics() {
		masterDataUpdateEventInitializer.register(this::isCacheEnabled);
	}

}
