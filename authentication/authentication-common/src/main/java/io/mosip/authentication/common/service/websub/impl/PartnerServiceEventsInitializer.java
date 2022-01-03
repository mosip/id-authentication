package io.mosip.authentication.common.service.websub.impl;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_PARTNER_SERVICE_CALLBACK_SECRET;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_PARTNER_SERVICE_CALLBACK_URL;

import java.util.Arrays;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.PartnerEventTypes;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.websub.api.model.SubscriptionChangeRequest;

/**
 * The Class PartnerServiceEventsInitializer.
 * @author Loganathan Sekar
 */
@Component
public class PartnerServiceEventsInitializer extends BaseWebSubEventsInitializer {
	
	/** The Constant logger. */
	private static final Logger logger = IdaLogger.getLogger(PartnerServiceEventsInitializer.class);
	
	/** The partner service callback URL. */
	@Value("${"+ IDA_WEBSUB_PARTNER_SERVICE_CALLBACK_URL +"}")
	private String partnerServiceCallbackURL;
	
	/** The partner service callback secret. */
	@Value("${"+ IDA_WEBSUB_PARTNER_SERVICE_CALLBACK_SECRET +"}")
	private String partnerServiceCallbackSecret;
	
	/**
	 * Do subscribe.
	 */
	@Override
	protected void doSubscribe() {
		logger.info(IdAuthCommonConstants.SESSION_ID, "doSubscribe",  this.getClass().getSimpleName(), "Initializing Partner Service event subscriptions..");
		subscribeForPartnerServiceEvents();		
	}
	
	/**
	 * Try register topic partner service events.
	 */
	private void tryRegisterTopicPartnerServiceEvents() {
		Arrays.stream(PartnerEventTypes.values()).forEach(eventType -> {
			String topic = env.getProperty(eventType.getTopicPropertyName());
			try {
				logger.debug(IdAuthCommonConstants.SESSION_ID, "tryRegisterTopicPartnerServiceEvents", "", "Trying to register topic: " + topic);
				webSubHelper.registerTopic(topic);
				logger.info(IdAuthCommonConstants.SESSION_ID, "tryRegisterTopicPartnerServiceEvents", "", "Registered topic: " + topic);
			} catch (Exception e) {
				logger.info(IdAuthCommonConstants.SESSION_ID, "tryRegisterTopicPartnerServiceEvents",  e.getClass().toString(), "Error registering topic: "+ topic +"\n" + e.getMessage());
			}
		});
			
	}
	
	/**
	 * Subscribe for partner service events.
	 */
	private void subscribeForPartnerServiceEvents() {
		Stream.of(PartnerEventTypes.values()).forEach(partnerEventType -> {
			String topic = env.getProperty(partnerEventType.getTopicPropertyName());
			try {
				SubscriptionChangeRequest subscriptionRequest = new SubscriptionChangeRequest();
				String callbackURL = partnerServiceCallbackURL.replace(EVENT_TYPE_PLACEHOLDER, partnerEventType.getName());
				subscriptionRequest.setCallbackURL(callbackURL);
				subscriptionRequest.setSecret(partnerServiceCallbackSecret);
				subscriptionRequest.setTopic(topic);
				logger.debug(IdAuthCommonConstants.SESSION_ID, "subscribeForAuthTypeEvents", "",
						"Trying to subscribe to topic: " + topic + " callback-url: "
								+ callbackURL);
				webSubHelper.subscribe(subscriptionRequest);
				logger.info(IdAuthCommonConstants.SESSION_ID, "subscribeForAuthTypeEvents", "",
						"Subscribed to topic: " + topic);
			} catch (Exception e) {
				logger.info(IdAuthCommonConstants.SESSION_ID, "subscribeForAuthTypeEvents", e.getClass().toString(),
						"Error subscribing topic: " + topic + "\n" + e.getMessage());
				throw e;
			}
		});
		
	}

	@Override
	protected void doRegister() {
		logger.info(IdAuthCommonConstants.SESSION_ID, "doRegister",  this.getClass().getSimpleName(), "Registering Partner Service event topic..");
		tryRegisterTopicPartnerServiceEvents();		
	}

}
