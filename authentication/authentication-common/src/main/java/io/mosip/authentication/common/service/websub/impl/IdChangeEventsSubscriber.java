package io.mosip.authentication.common.service.websub.impl;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_AUTH_PARTNER_ID;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_CREDENTIAL_ISSUE_CALLBACK_URL;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_CRED_ISSUE_CALLBACK_SECRET;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.idrepository.core.constant.IDAEventType;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.websub.api.model.SubscriptionChangeRequest;

/**
 * The Class IdChangeEventsSubscriber.
 * @author Loganathan Sekar
 */
@Component
public class IdChangeEventsSubscriber extends BaseWebSubEventsSubscriber {
	
	/** The Constant logger. */
	private static final Logger logger = IdaLogger.getLogger(IdChangeEventsSubscriber.class);
	
	/** The Constant ID_CHANGE_EVENTS. */
	private static final IDAEventType[] ID_CHANGE_EVENTS = {IDAEventType.CREDENTIAL_ISSUED, IDAEventType.REMOVE_ID, IDAEventType.DEACTIVATE_ID, IDAEventType.ACTIVATE_ID};
	
	/** The Constant PARTNER_ID_PLACEHOLDER. */
	private static final String PARTNER_ID_PLACEHOLDER = "{partnerId}";
	
	/** The credential issue callback URL. */
	@Value("${"+ IDA_WEBSUB_CREDENTIAL_ISSUE_CALLBACK_URL +"}")
	private String credentialIssueCallbackURL;
	
	/** The cred issue callbacksecret. */
	@Value("${"+ IDA_WEBSUB_CRED_ISSUE_CALLBACK_SECRET +"}")
	private String credIssueCallbacksecret;
	
	/** The auth parther id. */
	@Value("${"+ IDA_AUTH_PARTNER_ID  +"}")
	private String authPartherId;

	/**
	 * Do initialize.
	 */
	@Override
	protected void doInitialize() {
		logger.info(IdAuthCommonConstants.SESSION_ID, "doInitialize",  this.getClass().getSimpleName(), "Initializing Internal Auth subscribptions..");
		String topicPrefix = authPartherId + "/";
		initCredentialIssueanceEvent(topicPrefix);
	}
	
	/**
	 * Inits the credential issueance event.
	 *
	 * @param topicPrefix the topic prefix
	 */
	private void initCredentialIssueanceEvent(String topicPrefix) {
		tryRegisterTopicCredentialIssueanceEvents(topicPrefix);
		subscribeForCredentialIssueanceEvents(topicPrefix);
	}

	/**
	 * Try register topic credential issueance events.
	 *
	 * @param topicPrefix the topic prefix
	 */
	private void tryRegisterTopicCredentialIssueanceEvents(String topicPrefix) {
		Arrays.stream(ID_CHANGE_EVENTS).forEach(eventType -> {
			String topic = topicPrefix + eventType.toString();
			try {
				logger.debug(IdAuthCommonConstants.SESSION_ID, "tryRegisterTopicCredentialIssueanceEvents", "", "Trying to register topic: " + topic);
				publisher.registerTopic(topic, publisherUrl);
				logger.info(IdAuthCommonConstants.SESSION_ID, "tryRegisterTopicCredentialIssueanceEvents", "", "Registered topic: " + topic);
			} catch (Exception e) {
				logger.info(IdAuthCommonConstants.SESSION_ID, "tryRegisterTopicCredentialIssueanceEvents",  e.getClass().toString(), "Error registering topic: "+ topic +"\n" + e.getMessage());
			}
		});
			
	}
	

	/**
	 * Subscribe for credential issueance events.
	 *
	 * @param topicPrefix the topic prefix
	 */
	private void subscribeForCredentialIssueanceEvents(String topicPrefix) {
		Arrays.stream(ID_CHANGE_EVENTS).forEach(eventType -> {
			String topic = topicPrefix + eventType.toString();
			try {
				SubscriptionChangeRequest subscriptionRequest = new SubscriptionChangeRequest();
				String callbackURL = credentialIssueCallbackURL.replace(PARTNER_ID_PLACEHOLDER, authPartherId)
														.replace(EVENT_TYPE_PLACEHOLDER, eventType.toString().toLowerCase());
				subscriptionRequest.setCallbackURL(callbackURL);
				subscriptionRequest.setHubURL(hubURL);
				subscriptionRequest.setSecret(credIssueCallbacksecret);
				subscriptionRequest.setTopic(topic);
				logger.debug(IdAuthCommonConstants.SESSION_ID, "subscribeForCredentialIssueanceEvents", "", "Trying to subscribe to topic: " + topic);
				subscribe.subscribe(subscriptionRequest);
				logger.info(IdAuthCommonConstants.SESSION_ID, "subscribeForCredentialIssueanceEvents", "", "Subscribed to topic: " + topic);
			} catch (Exception e) {
				logger.info(IdAuthCommonConstants.SESSION_ID, "subscribeForCredentialIssueanceEvents",  e.getClass().toString(), "Error subscribing topic: "+ topic +"\n" + e.getMessage());
				throw e;
			}
		});
					
	}

}
