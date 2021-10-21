package io.mosip.authentication.common.service.websub.impl;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.websub.api.model.SubscriptionChangeRequest;

/**
 * The Class PartnerCACertEventSubscriber.
 * 
 * @author Manoj SP
 */
@Component
public class HotlistEventSubscriber extends BaseWebSubEventsSubscriber {

	/** The Constant logger. */
	private static final Logger logger = IdaLogger.getLogger(HotlistEventSubscriber.class);

	/** The partner service callback URL. */
	@Value("${" + IDA_WEBSUB_HOTLIST_CALLBACK_URL + "}")
	private String hotlistCallbackURL;

	/** The partner service callback secret. */
	@Value("${" + IDA_WEBSUB_HOTLIST_CALLBACK_SECRET + "}")
	private String hotlistCallbackSecret;

	@Value("${" + IDA_WEBSUB_HOTLIST_TOPIC + "}")
	private String hotlistEventTopic;

	/**
	 * Do initialize.
	 */
	@Override
	protected void doInitialize() {
		logger.info(IdAuthCommonConstants.SESSION_ID, "doInitialize", this.getClass().getSimpleName(),
				"Initializing hotlist event subscriptions..");
		tryRegisterTopicHotlistEvent();
		subscribeForHotlistEvent();
	}

	/**
	 * Try register topic partner service events.
	 */
	private void tryRegisterTopicHotlistEvent() {
		try {
			logger.debug(IdAuthCommonConstants.SESSION_ID, "tryRegisterTopicHotlistEvent", "",
					"Trying to register topic: " + hotlistEventTopic);
			publisher.registerTopic(hotlistEventTopic, publisherUrl);
			logger.info(IdAuthCommonConstants.SESSION_ID, "tryRegisterTopicHotlistEvent", "",
					"Registered topic: " + hotlistEventTopic);
		} catch (Exception e) {
			logger.info(IdAuthCommonConstants.SESSION_ID, "tryRegisterTopicHotlistEvent", e.getClass().toString(),
					"Error registering topic: " + hotlistEventTopic + "\n" + e.getMessage());
		}
	}

	/**
	 * Subscribe for partner service events.
	 */
	private void subscribeForHotlistEvent() {
		try {
			SubscriptionChangeRequest subscriptionRequest = new SubscriptionChangeRequest();
			subscriptionRequest.setCallbackURL(hotlistCallbackURL);
			subscriptionRequest.setHubURL(hubURL);
			subscriptionRequest.setSecret(hotlistCallbackSecret);
			subscriptionRequest.setTopic(hotlistEventTopic);
			logger.debug(IdAuthCommonConstants.SESSION_ID, "subscribeForHotlistEvent", "",
					"Trying to subscribe to topic: " + hotlistEventTopic + " callback-url: "
							+ hotlistCallbackURL);
			subscribe.subscribe(subscriptionRequest);
			logger.info(IdAuthCommonConstants.SESSION_ID, "subscribeForHotlistEvent", "",
					"Subscribed to topic: " + hotlistEventTopic);
		} catch (Exception e) {
			logger.info(IdAuthCommonConstants.SESSION_ID, "subscribeForHotlistEvent", e.getClass().toString(),
					"Error subscribing topic: " + hotlistEventTopic + "\n" + e.getMessage());
			throw e;
		}
	}
}
