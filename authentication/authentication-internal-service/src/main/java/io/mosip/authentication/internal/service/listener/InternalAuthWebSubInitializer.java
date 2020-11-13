package io.mosip.authentication.internal.service.listener;

import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.config.IdAuthWebSubInitializer;

@Component
public class InternalAuthWebSubInitializer extends IdAuthWebSubInitializer{
	

	protected void doInitSubscriptions() {
		super.doInitSubscriptions();
		webSubSubscriptionHelper.initInternalAuthSubsriptions();
	}

}
