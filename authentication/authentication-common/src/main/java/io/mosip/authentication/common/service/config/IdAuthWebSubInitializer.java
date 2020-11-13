package io.mosip.authentication.common.service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.helper.WebSubSubscriptionHelper;

/**
 * @author Manoj SP
 *
 */
@Component
public class IdAuthWebSubInitializer implements ApplicationListener<ApplicationReadyEvent> {

	@Autowired
	private WebSubSubscriptionHelper webSubHelper;

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
//		webSubHelper.initAuthSubsriptions();
	}
}
