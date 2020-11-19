package io.mosip.authentication.common.service.helper;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_AUTH_PARTNER_ID;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_AUTHTYPE_CALLBACK_SECRET;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_AUTH_TYPE_CALLBACK_URL;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_CREDENTIAL_ISSUE_CALLBACK_URL;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_CRED_ISSUE_CALLBACK_SECRET;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_HUB_URL;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_PARTNER_SERVICE_CALLBACK_SECRET;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_PARTNER_SERVICE_CALLBACK_URL;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_PUBLISHER_URL;

import java.util.Arrays;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.PartnerEventTypes;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.idrepository.core.constant.IDAEventType;
import io.mosip.idrepository.core.dto.EventModel;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.websub.spi.PublisherClient;
import io.mosip.kernel.core.websub.spi.SubscriptionClient;
import io.mosip.kernel.websub.api.model.SubscriptionChangeRequest;
import io.mosip.kernel.websub.api.model.SubscriptionChangeResponse;
import io.mosip.kernel.websub.api.model.UnsubscriptionRequest;

@Component
public class WebSubSubscriptionHelper {

	private static final String PARTNER_ID_PLACEHOLDER = "{partnerId}";
	private static final String EVENT_TYPE_PLACEHOLDER = "{eventType}";
	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(WebSubSubscriptionHelper.class);
	
	private static final IDAEventType[] ID_CHANGE_EVENTS = {IDAEventType.CREDENTIAL_ISSUED, IDAEventType.REMOVE_ID, IDAEventType.DEACTIVATE_ID, IDAEventType.ACTIVATE_ID};
	
	@Value("${"+ IDA_WEBSUB_HUB_URL +"}")
	private String hubURL;
	
	@Value("${"+ IDA_WEBSUB_PUBLISHER_URL +"}")
	private String publisherUrl;
	
	@Value("${"+ IDA_WEBSUB_PARTNER_SERVICE_CALLBACK_URL +"}")
	private String partnerServiceCallbackURL;
	
	@Value("${"+ IDA_WEBSUB_AUTH_TYPE_CALLBACK_URL +"}")
	private String authTypeCallbackURL;
	
	@Value("${"+ IDA_WEBSUB_CREDENTIAL_ISSUE_CALLBACK_URL +"}")
	private String credentialIssueCallbackURL;
	
	@Value("${"+ IDA_WEBSUB_AUTHTYPE_CALLBACK_SECRET +"}")
	private String autypeCallbackSecret;
	
	@Value("${"+ IDA_WEBSUB_PARTNER_SERVICE_CALLBACK_SECRET +"}")
	private String partnerServiceCallbackSecret;
	
	@Value("${"+ IDA_WEBSUB_CRED_ISSUE_CALLBACK_SECRET +"}")
	private String credIssueCallbacksecret;
	
	@Autowired
	private PublisherClient<String, EventModel, HttpHeaders> publisher;
	
	@Autowired
	private SubscriptionClient<SubscriptionChangeRequest, UnsubscriptionRequest, SubscriptionChangeResponse> subscribe;
	
	@Value("${"+ IDA_AUTH_PARTNER_ID  +"}")
	private String authPartherId;
	
	@Autowired
	private Environment env;
	
	public void initInternalAuthSubsriptions() {
		logger.info(IdAuthCommonConstants.SESSION_ID, "onApplicationEvent",  "", "Initializing Internal Auth subscribptions..");
		String topicPrefix = authPartherId + "/";
		initAuthTypeEvent(topicPrefix);	
		initCredentialIssueanceEvent(topicPrefix);
	}
	
	public void initAuthSubsriptions() {
		logger.info(IdAuthCommonConstants.SESSION_ID, "onApplicationEvent",  "", "Initializing Auth subscriptions..");
		initPartnerServiceEvents();
	}

	private void initPartnerServiceEvents() {
		tryRegisterTopicPartnerServiceEvents();
		subscribeForPartnerServiceEvents();
	}
	
	private void tryRegisterTopicForAuthEvents(String topicPrefix) {
		String topic = topicPrefix + IDAEventType.AUTH_TYPE_STATUS_UPDATE.name();
		try {
			logger.debug(IdAuthCommonConstants.SESSION_ID, "tryRegisterTopicForAuthEvents", "", "Trying to register topic: " + topic);
			publisher.registerTopic(topic, publisherUrl);	
			logger.info(IdAuthCommonConstants.SESSION_ID, "tryRegisterTopicForAuthEvents", "", "Registered topic: " + topic);
		} catch (Exception e) {
			logger.info(IdAuthCommonConstants.SESSION_ID, "tryRegisterTopicForAuthEvents",  e.getClass().toString(), "Error registering topic: "+ topic +"\n" + e.getMessage());
		}
	}

	private void subscribeForAuthTypeEvents(String topicPrefix) {
		String topic = topicPrefix + IDAEventType.AUTH_TYPE_STATUS_UPDATE.name();
		try {
			SubscriptionChangeRequest subscriptionRequest = new SubscriptionChangeRequest();
			subscriptionRequest.setCallbackURL(authTypeCallbackURL.replace(PARTNER_ID_PLACEHOLDER, authPartherId));
			subscriptionRequest.setHubURL(hubURL);
			subscriptionRequest.setSecret(autypeCallbackSecret);
			subscriptionRequest.setTopic(topic);
			logger.debug(IdAuthCommonConstants.SESSION_ID, "subscribeForAuthTypeEvents", "", "Trying to subscribe to topic: " + topic + " callback-url: " + authTypeCallbackURL);
			subscribe.subscribe(subscriptionRequest);
			logger.info(IdAuthCommonConstants.SESSION_ID, "subscribeForAuthTypeEvents", "", "Subscribed to topic: " + topic);
		} catch (Exception e) {
			logger.info(IdAuthCommonConstants.SESSION_ID, "subscribeForAuthTypeEvents",  e.getClass().toString(), "Error subscribing topic: "+ topic +"\n" + e.getMessage());
			throw e;
		}
	}

	private void initAuthTypeEvent(String topicPrefix) {
		tryRegisterTopicForAuthEvents(topicPrefix);
		subscribeForAuthTypeEvents(topicPrefix);
	}

	private void initCredentialIssueanceEvent(String topicPrefix) {
		tryRegisterTopicCredentialIssueanceEvents(topicPrefix);
		subscribeForCredentialIssueanceEvents(topicPrefix);
	}

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
	
	private void tryRegisterTopicPartnerServiceEvents() {
		Arrays.stream(PartnerEventTypes.values()).forEach(eventType -> {
			String topic = env.getProperty(eventType.getTopicPropertyName());
			try {
				logger.debug(IdAuthCommonConstants.SESSION_ID, "tryRegisterTopicPartnerServiceEvents", "", "Trying to register topic: " + topic);
				publisher.registerTopic(topic, publisherUrl);
				logger.info(IdAuthCommonConstants.SESSION_ID, "tryRegisterTopicPartnerServiceEvents", "", "Registered topic: " + topic);
			} catch (Exception e) {
				logger.info(IdAuthCommonConstants.SESSION_ID, "tryRegisterTopicPartnerServiceEvents",  e.getClass().toString(), "Error registering topic: "+ topic +"\n" + e.getMessage());
			}
		});
			
	}

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
	
	private void subscribeForPartnerServiceEvents() {
		Stream.of(PartnerEventTypes.values()).forEach(partnerEventType -> {
			String topic = env.getProperty(partnerEventType.getTopicPropertyName());
			try {
				SubscriptionChangeRequest subscriptionRequest = new SubscriptionChangeRequest();
				subscriptionRequest.setCallbackURL(partnerServiceCallbackURL.replace(EVENT_TYPE_PLACEHOLDER, partnerEventType.getName()));
				subscriptionRequest.setHubURL(hubURL);
				subscriptionRequest.setSecret(partnerServiceCallbackSecret);
				subscriptionRequest.setTopic(topic);
				logger.debug(IdAuthCommonConstants.SESSION_ID, "subscribeForAuthTypeEvents", "",
						"Trying to subscribe to topic: " + topic + " callback-url: "
								+ partnerServiceCallbackURL);
				subscribe.subscribe(subscriptionRequest);
				logger.info(IdAuthCommonConstants.SESSION_ID, "subscribeForAuthTypeEvents", "",
						"Subscribed to topic: " + topic);
			} catch (Exception e) {
				logger.info(IdAuthCommonConstants.SESSION_ID, "subscribeForAuthTypeEvents", e.getClass().toString(),
						"Error subscribing topic: " + topic + "\n" + e.getMessage());
				throw e;
			}
		});
		
	}

}
