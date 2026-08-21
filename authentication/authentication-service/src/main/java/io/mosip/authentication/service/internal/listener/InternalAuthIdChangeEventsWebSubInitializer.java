package io.mosip.authentication.service.internal.listener;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.helper.WebSubHelper;
import io.mosip.authentication.common.service.websub.impl.IdChangeEventsInitializer;

/**
 * On-demand id-change-event websub subscription check used by the credential
 * store retrigger batch job (ValidateWebSubTasklet). Deliberately does NOT
 * extend BaseIDAWebSubInitializer/CacheUpdatingWebsubInitializer: that base
 * class auto-subscribes every concrete subclass at ApplicationReadyEvent, and
 * idChangeEventsInitializer is already subscribed once at startup by
 * IdAuthWebSubInitializer - extending it here would double-subscribe.
 */
@Component
public class InternalAuthIdChangeEventsWebSubInitializer {

	@Autowired
	private WebSubHelper webSubHelper;

	@Autowired
	private IdChangeEventsInitializer idChangeEventInitializer;

	public int doInitSubscriptions() {
		return webSubHelper.initSubscriber(idChangeEventInitializer);
	}

	public int doRegisterTopics() {
		return HttpStatus.SC_OK;
	}
}
