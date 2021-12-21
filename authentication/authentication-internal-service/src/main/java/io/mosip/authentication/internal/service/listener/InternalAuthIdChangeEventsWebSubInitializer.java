package io.mosip.authentication.internal.service.listener;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.websub.CacheUpdatingWebsubInitializer;
import io.mosip.authentication.common.service.websub.impl.IdChangeEventsInitializer;

/**
 * The Class InternalAuthWebSubInitializer.
 * 
 * @author Manoj SP
 */
@Component
public class InternalAuthIdChangeEventsWebSubInitializer extends CacheUpdatingWebsubInitializer {

	/** The id change event initializer. */
	@Autowired
	private IdChangeEventsInitializer idChangeEventInitializer;

	/**
	 * Do init subscriptions.
	 */
	public int doInitSubscriptions() {
		return webSubHelper.initSubscriber(idChangeEventInitializer);
	}

	/**
	 * Do register topics.
	 */
	@Override
	public int doRegisterTopics() {
		//webSubHelper.initRegistrar(idChangeEventInitializer);
		return HttpStatus.SC_OK;
	}

}
