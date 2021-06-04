package io.mosip.authentication.common.service.websub.impl;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_HUB_URL;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_PUBLISHER_URL;

import java.util.function.Supplier;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.websub.WebSubEventSubcriber;
import io.mosip.authentication.common.service.websub.WebSubEventTopicRegistrar;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.exception.IdAuthRetryException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.retry.WithRetry;
import io.mosip.kernel.core.websub.model.EventModel;
import io.mosip.kernel.core.websub.spi.PublisherClient;
import io.mosip.kernel.core.websub.spi.SubscriptionClient;
import io.mosip.kernel.websub.api.exception.WebSubClientException;
import io.mosip.kernel.websub.api.model.SubscriptionChangeRequest;
import io.mosip.kernel.websub.api.model.SubscriptionChangeResponse;
import io.mosip.kernel.websub.api.model.UnsubscriptionRequest;

/**
 * The Class BaseWebSubEventsInitializer.
 * @author Loganathan Sekar
 */
@Component
public abstract class BaseWebSubEventsInitializer implements WebSubEventTopicRegistrar, WebSubEventSubcriber {
	
	private static final Logger logger = IdaLogger.getLogger(BaseWebSubEventsInitializer.class);
	
	/** The Constant EVENT_TYPE_PLACEHOLDER. */
	protected static final String EVENT_TYPE_PLACEHOLDER = "{eventType}";
	
	/** The hub URL. */
	@Value("${"+ IDA_WEBSUB_HUB_URL +"}")
	protected String hubURL;
	
	/** The publisher url. */
	@Value("${"+ IDA_WEBSUB_PUBLISHER_URL +"}")
	protected String publisherUrl;
	
	/** The env. */
	@Autowired
	protected Environment env;
	
	/** The publisher. */
	@Autowired
	protected PublisherClient<String, EventModel, HttpHeaders> publisher;
	
	/** The subscribe. */
	@Autowired
	protected SubscriptionClient<SubscriptionChangeRequest, UnsubscriptionRequest, SubscriptionChangeResponse> subscribe;
	

	/**
	 * Subscribe.
	 *
	 * @param enableTester the enable tester
	 */
	@Override
	@WithRetry
	public void subscribe(Supplier<Boolean> enableTester) {
		if(enableTester == null || enableTester.get()) {
			try {
				doSubscribe();
			} catch (WebSubClientException e) {
				logger.error(IdAuthCommonConstants.SESSION_ID, "subscribe",  this.getClass().getSimpleName(), ExceptionUtils.getStackTrace(e));
				throw new IdAuthRetryException(e);
			}
		} else {
			logger.info(IdAuthCommonConstants.SESSION_ID, "subscribe",  this.getClass().getSimpleName(), "This websub subscriber is disabled.");
		}
	}
	
	/**
	 * Do subscribe.
	 */
	protected abstract void doSubscribe();
	
	/**
	 * Register.
	 *
	 * @param enableTester the enable tester
	 */
	@Override
	public void register(Supplier<Boolean> enableTester) {
		if(enableTester == null || enableTester.get()) {
			doRegister();
		} else {
			logger.info(IdAuthCommonConstants.SESSION_ID, "register",  this.getClass().getSimpleName(), "This websub subscriber is disabled.");
		}
	}
	
	/**
	 * Do register.
	 */
	protected abstract void doRegister();
	
	protected void tryRegisterTopicEvent(String eventTopic) {
		try {
			logger.debug(this.getClass().getCanonicalName(), "tryRegisterTopicEvent", "",
					"Trying to register topic: " + eventTopic);
			publisher.registerTopic(eventTopic, publisherUrl);
			logger.info(this.getClass().getCanonicalName(), "tryRegisterTopicEvent", "",
					"Registered topic: " + eventTopic);
		} catch (Exception e) {
			logger.info(this.getClass().getCanonicalName(), "tryRegisterTopicEvent", e.getClass().toString(),
					"Error registering topic: " + eventTopic + "\n" + e.getMessage());
		}
	}

	protected void subscribeForEvent(String eventTopic, String callbackUrl, String callbackSecret) {
		try {
			SubscriptionChangeRequest subscriptionRequest = new SubscriptionChangeRequest();
			subscriptionRequest.setCallbackURL(callbackUrl);
			subscriptionRequest.setHubURL(hubURL);
			subscriptionRequest.setSecret(callbackSecret);
			subscriptionRequest.setTopic(eventTopic);
			logger.debug(IdAuthCommonConstants.SESSION_ID, "subscribeForHotlistEvent", "",
					"Trying to subscribe to topic: " + eventTopic + " callback-url: "
							+ callbackUrl);
			subscribe.subscribe(subscriptionRequest);
			logger.info(IdAuthCommonConstants.SESSION_ID, "subscribeForHotlistEvent", "",
					"Subscribed to topic: " + eventTopic);
		} catch (Exception e) {
			logger.info(IdAuthCommonConstants.SESSION_ID, "subscribeForHotlistEvent", e.getClass().toString(),
					"Error subscribing topic: " + eventTopic + "\n" + e.getMessage());
			throw e;
		}
	}

}
