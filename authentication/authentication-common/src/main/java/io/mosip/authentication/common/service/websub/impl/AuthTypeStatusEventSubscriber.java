package io.mosip.authentication.common.service.websub.impl;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_AUTH_PARTNER_ID;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_AUTHTYPE_CALLBACK_SECRET;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_AUTH_TYPE_CALLBACK_URL;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.idrepository.core.constant.IDAEventType;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.websub.api.model.SubscriptionChangeRequest;

/**
 * The Class AuthTypeStatusEventSubscriber.
 * @author Loganathan Sekar
 */
@Component
public class AuthTypeStatusEventSubscriber extends BaseWebSubEventsInitializer {
	
	private static final String SUBSCRIBE_FOR_AUTH_TYPE_EVENTS = "subscribeForAuthTypeEvents";

	private static final String TRY_REGISTER_TOPIC_FOR_AUTH_EVENTS = "tryRegisterTopicForAuthEvents";

	/** The Constant logger. */
	private static final Logger logger = IdaLogger.getLogger(AuthTypeStatusEventSubscriber.class);
	
	/** The Constant PARTNER_ID_PLACEHOLDER. */
	private static final String PARTNER_ID_PLACEHOLDER = "{partnerId}";
	
	/** The auth type callback URL. */
	@Value("${"+ IDA_WEBSUB_AUTH_TYPE_CALLBACK_URL +"}")
	private String authTypeCallbackURL;
	
	
	/** The autype callback secret. */
	@Value("${"+ IDA_WEBSUB_AUTHTYPE_CALLBACK_SECRET +"}")
	private String autypeCallbackSecret;
	
	/** The auth parther id. */
	@Value("${"+ IDA_AUTH_PARTNER_ID  +"}")
	private String authPartherId;

	/**
	 * Do initialize.
	 */
	@Override
	protected void doSubscribe() {
		logger.info(IdAuthCommonConstants.SESSION_ID, "doSubscribe",  this.getClass().getSimpleName(), "Initializing Internal Auth subscribptions..");
		String topicPrefix = authPartherId + "/";
		subscribeForAuthTypeEvents(topicPrefix);
	}
	
	/**
	 * Try register topic for auth events.
	 *
	 * @param topicPrefix the topic prefix
	 */
	private void tryRegisterTopicForAuthEvents(String topicPrefix) {
		String topic = topicPrefix + IDAEventType.AUTH_TYPE_STATUS_UPDATE.name();
		try {
			logger.debug(IdAuthCommonConstants.SESSION_ID, TRY_REGISTER_TOPIC_FOR_AUTH_EVENTS, "", "Trying to register topic: " + topic);
			webSubHelper.registerTopic(topic);	
			logger.info(IdAuthCommonConstants.SESSION_ID, TRY_REGISTER_TOPIC_FOR_AUTH_EVENTS, "", "Registered topic: " + topic);
		} catch (Exception e) {
			logger.info(IdAuthCommonConstants.SESSION_ID, TRY_REGISTER_TOPIC_FOR_AUTH_EVENTS,  e.getClass().toString(), "Error registering topic: "+ topic +"\n" + e.getMessage());
		}
	}

	/**
	 * Subscribe for auth type events.
	 *
	 * @param topicPrefix the topic prefix
	 */
	private void subscribeForAuthTypeEvents(String topicPrefix) {
		String topic = topicPrefix + IDAEventType.AUTH_TYPE_STATUS_UPDATE.name();
		try {
			SubscriptionChangeRequest subscriptionRequest = new SubscriptionChangeRequest();
			subscriptionRequest.setCallbackURL(authTypeCallbackURL.replace(PARTNER_ID_PLACEHOLDER, authPartherId));
			subscriptionRequest.setSecret(autypeCallbackSecret);
			subscriptionRequest.setTopic(topic);
			logger.debug(IdAuthCommonConstants.SESSION_ID, SUBSCRIBE_FOR_AUTH_TYPE_EVENTS, "", "Trying to subscribe to topic: " + topic + " callback-url: " + authTypeCallbackURL);
			webSubHelper.subscribe(subscriptionRequest);
			logger.info(IdAuthCommonConstants.SESSION_ID, SUBSCRIBE_FOR_AUTH_TYPE_EVENTS, "", "Subscribed to topic: " + topic);
		} catch (Exception e) {
			logger.info(IdAuthCommonConstants.SESSION_ID, SUBSCRIBE_FOR_AUTH_TYPE_EVENTS,  e.getClass().toString(), "Error subscribing topic: "+ topic +"\n" + e.getMessage());
			throw e;
		}
	}

	@Override
	protected void doRegister() {
		logger.info(IdAuthCommonConstants.SESSION_ID, "doRegister",  this.getClass().getSimpleName(), "Registering Auth Type Status topic..");
		String topicPrefix = authPartherId + "/";
		tryRegisterTopicForAuthEvents(topicPrefix);
	}

}
