package io.mosip.authentication.common.service.websub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.websub.impl.MasterDataServiceEventSubscriber;
import io.mosip.authentication.common.service.websub.impl.PartnerServiceEventsSubscriber;

/**
 * 
 * Websub Initializer for External facing IDA services such as Auth, EKYC and
 * OTP services
 * 
 * @author Loganathan Sekar
 * @author Manoj SP
 *
 */
 
@Component
public final class IdAuthWebSubInitializer extends CacheUpdatingWebsubInitializer {
	
	@Autowired
	private PartnerServiceEventsSubscriber partnerServiceEventsSubscriber;
	
	@Autowired
	private MasterDataServiceEventSubscriber masterDataServiceEventSubscriber;

	protected void doInitSubscriptions() {
		webSubSubscriptionHelper.initSubscriber(partnerServiceEventsSubscriber, this::isCacheEnabled);
		webSubSubscriptionHelper.initSubscriber(masterDataServiceEventSubscriber, this::isCacheEnabled);
		
	}
	
}
