package io.mosip.authentication.internal.service.validator;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_AUTHTYPE_CALLBACK_SECRET;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_AUTH_TYPE_CALLBACK_URL;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_CREDENTIAL_ISSUE_CALLBACK_URL;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_CRED_ISSUE_CALLBACK_SECRET;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_HUB_URL;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_PUBLISHER_URL;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.integration.PartnerServiceManager;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
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
public class InternalAuthApplicationListener implements ApplicationListener<ApplicationReadyEvent>{
	
	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(InternalAuthApplicationListener.class);
	
	private static final IDAEventType[] ID_CHANGE_EVENTS = {IDAEventType.CREDENTIAL_ISSUED, IDAEventType.REMOVE_ID, IDAEventType.DEACTIVATE_ID, IDAEventType.ACTIVATE_ID};

	@Value("${"+ IDA_WEBSUB_HUB_URL +"}")
	private String hubURL;
	
	@Value("${"+ IDA_WEBSUB_PUBLISHER_URL +"}")
	private String publisherUrl;
	
	@Value("${"+ IDA_WEBSUB_AUTH_TYPE_CALLBACK_URL +"}")
	private String authTypeCallbackURL;
	
	@Value("${"+ IDA_WEBSUB_CREDENTIAL_ISSUE_CALLBACK_URL +"}")
	private String credentialIssueCallbackURL;
	
	@Value("${"+ IDA_WEBSUB_AUTHTYPE_CALLBACK_SECRET +"}")
	private String autypeCallbackSecret;
	
	@Value("${"+ IDA_WEBSUB_CRED_ISSUE_CALLBACK_SECRET +"}")
	private String credIssueCallbacksecret;
	
	@Autowired
	private PartnerServiceManager partnerServiceManager;
	
	@Autowired
	private PublisherClient<String, EventModel, HttpHeaders> publisher; 
	
	@Autowired
	SubscriptionClient<SubscriptionChangeRequest, UnsubscriptionRequest, SubscriptionChangeResponse> subscribe; 
	
	private void tryRegisterTopicForAuthEvents() {
		String topic = IDAEventType.AUTH_TYPE_STATUS_UPDATE.name();
		try {
			logger.debug(IdAuthCommonConstants.SESSION_ID, "tryRegisterTopicForAuthEvents", "", "Trying to register topic: " + topic);
			publisher.registerTopic(topic, publisherUrl);	
			logger.info(IdAuthCommonConstants.SESSION_ID, "tryRegisterTopicForAuthEvents", "", "Registered topic: " + topic);
		} catch (Exception e) {
			logger.info(IdAuthCommonConstants.SESSION_ID, "tryRegisterTopicForAuthEvents",  e.getClass().toString(), "Error registering topic: "+ topic +"\n" + e.getMessage());
		}
	}

	private void subscribeForAuthTypeEvents() {
		String topic = IDAEventType.AUTH_TYPE_STATUS_UPDATE.name();
		try {
			SubscriptionChangeRequest subscriptionRequest = new SubscriptionChangeRequest();
			subscriptionRequest.setCallbackURL(authTypeCallbackURL);
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
	
	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		initAuthTypeEvent();	
		initCredentialIssueanceEvent();
	}

	private void initAuthTypeEvent() {
		tryRegisterTopicForAuthEvents();
		subscribeForAuthTypeEvents();
	}
	
	
	//@PostConstruct
		public void initCredentialIssueanceEvent() {
			List<String> partnerIds = partnerServiceManager.getPartnerIds();
			tryRegisterTopicCredentialIssueanceEvents(partnerIds);
			subscribeForCredentialIssueanceEvents(partnerIds);
		}
		
		private void tryRegisterTopicCredentialIssueanceEvents(List<String> partnerIds) {
			partnerIds.forEach(partnerId -> {
				
				Arrays.stream(ID_CHANGE_EVENTS).forEach(eventType -> {
					String topic = partnerId + "/" + eventType.toString();
					try {
						logger.debug(IdAuthCommonConstants.SESSION_ID, "tryRegisterTopicCredentialIssueanceEvents", "", "Trying to register topic: " + topic);
						publisher.registerTopic(topic, publisherUrl);
						logger.info(IdAuthCommonConstants.SESSION_ID, "tryRegisterTopicCredentialIssueanceEvents", "", "Registered topic: " + topic);
					} catch (Exception e) {
						logger.info(IdAuthCommonConstants.SESSION_ID, "tryRegisterTopicCredentialIssueanceEvents",  e.getClass().toString(), "Error registering topic: "+ topic +"\n" + e.getMessage());
					}
				});
				
			});
		}

		private void subscribeForCredentialIssueanceEvents(List<String> partnerIds) {
					partnerIds.forEach(partnerId -> {
						
						Arrays.stream(ID_CHANGE_EVENTS).forEach(eventType -> {
							String topic = partnerId + "/" + eventType.toString();
							try {
								SubscriptionChangeRequest subscriptionRequest = new SubscriptionChangeRequest();
								subscriptionRequest.setCallbackURL(credentialIssueCallbackURL);
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
						
					});
		}

}
