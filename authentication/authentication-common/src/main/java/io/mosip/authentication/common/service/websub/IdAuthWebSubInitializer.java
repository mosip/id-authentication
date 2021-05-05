package io.mosip.authentication.common.service.websub;

import org.springframework.stereotype.Component;

/**
 * Websub Initializer for External facing IDA services such as Auth, EKYC and
 * OTP services.
 *
 * @author Loganathan Sekar
 * @author Manoj SP
 */

@Component
public final class IdAuthWebSubInitializer extends CacheUpdatingWebsubInitializer {

	/**
	 * Do init subscriptions.
	 */
	@Override
	protected void doInitSubscriptions() {
	}

	/**
	 * Do register topics.
	 */
	@Override
	protected void doRegisterTopics() {
	}

}
