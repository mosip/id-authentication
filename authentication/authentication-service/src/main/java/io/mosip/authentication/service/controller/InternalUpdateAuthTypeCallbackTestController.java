package io.mosip.authentication.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.idrepository.core.constant.IDAEventType;
import io.mosip.idrepository.core.dto.EventModel;
import io.mosip.idrepository.core.dto.IDAEventsDTO;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.websub.spi.PublisherClient;
import io.mosip.kernel.core.websub.spi.SubscriptionClient;
import io.mosip.kernel.websub.api.annotation.PreAuthenticateContentAndVerifyIntent;
import io.mosip.kernel.websub.api.model.SubscriptionChangeRequest;
import io.mosip.kernel.websub.api.model.SubscriptionChangeResponse;
import io.mosip.kernel.websub.api.model.UnsubscriptionRequest;

/**
 * The InternalUpdateAuthTypeController use to fetch Auth Transaction.
 *
 * @author Dinesh Karuppiah.T
 */
@RestController
public class InternalUpdateAuthTypeCallbackTestController {

	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(InternalUpdateAuthTypeCallbackTestController.class);

//	@Autowired
//	private UpdateAuthtypeStatusService authtypeStatusService;

	@Autowired
	private AuditHelper auditHelper;
	
	@Value("${"+ IdAuthConfigKeyConstants.IDA_WEBSUB_HUB_URL +"}")
	private String hubURL;
	
	@Value("${"+ IdAuthConfigKeyConstants.IDA_WEBSUB_PUBLISHER_URL +"}")
	private String publisherUrl;
	
	@Value("${"+ IdAuthConfigKeyConstants.IDA_WEBSUB_AUTH_TYPE_CALLBACK_URL +"}")
	private String authTypeCallbackURL;
	
	@Value("${"+ IdAuthConfigKeyConstants.IDA_WEBSUB_CREDENTIAL_ISSUE_CALLBACK_URL +"}")
	private String credentialIssueCallbackURL;
	
	@Value("${"+ IdAuthConfigKeyConstants.IDA_WEBSUB_SECRET +"}")
	private String secret;
	
	@Autowired
	private PublisherClient<String, EventModel, HttpHeaders> publisher; 
	
	@Autowired
	SubscriptionClient<SubscriptionChangeRequest, UnsubscriptionRequest, SubscriptionChangeResponse> subscribe; 
	
	//@PostConstruct
	public void init() {
		tryRegisterTopicForAuthEvents();
		subscribeForAuthTypeEvents();
	}
	
	@GetMapping(value = "/authTypeCallback")
	public ResponseEntity<String> updateAuthtypeStatusIntentVerifier(
			@RequestParam(name = "intentMode", required = false) String intentMode,
			@RequestParam(name = "hub.mode", required = false) String mode,
			@RequestParam(name = "hub.topic", required = false) String topic,
			@RequestParam(name = "hub.challenge", required = false) String challenge,
			@RequestParam(name = "hub.lease_seconds", required = false) String leaseSecs
			)
			throws IdAuthenticationAppException, IDDataValidationException {
		logger.debug(IdAuthCommonConstants.SESSION_ID, "updateAuthtypeStatusIntentVerifier", "", "inside Intent verifier of credentialIssueanceCallback \n "
				+ "intentMode: " + intentMode + "\n"
				+ "mode: " + mode + "\n"
				+ "topic: " + topic + "\n"
				+ "challenge: " + challenge + "\n"
				+ "lease_seconds: " + leaseSecs);
		return ResponseEntity.ok().body(challenge == null ? "" : challenge);
	}
	
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
			String baseUrl = "https://dev.mosip.net/idauthentication/v1/auth";
			String callbackUrl = baseUrl+"/authTypeCallback";
			subscriptionRequest.setCallbackURL(callbackUrl);
			subscriptionRequest.setHubURL(hubURL);
			subscriptionRequest.setSecret(secret);
			subscriptionRequest.setTopic(topic);
			logger.debug(IdAuthCommonConstants.SESSION_ID, "subscribeForAuthTypeEvents", "", "Trying to subscribe to topic: " + topic + " callback-url: " + callbackUrl);
			subscribe.subscribe(subscriptionRequest);
			logger.info(IdAuthCommonConstants.SESSION_ID, "subscribeForAuthTypeEvents", "", "Subscribed to topic: " + topic);
		} catch (Exception e) {
			logger.info(IdAuthCommonConstants.SESSION_ID, "subscribeForAuthTypeEvents",  e.getClass().toString(), "Error subscribing topic: "+ topic +"\n" + e.getMessage());
			throw e;
		}
	}
	
	@PostMapping(value = "/initAuthTypeEventSubsriptions")
	public ResponseEntity<?> initSubsriptions()
			throws IdAuthenticationAppException, IDDataValidationException {
		logger.debug(IdAuthCommonConstants.SESSION_ID, "initSubsriptions", "", "Inside initializing subscriptions api");
		init();
		return ResponseEntity.ok().build();
	}
	
	@PostMapping(value = "/authTypeCallback", consumes = "application/json")
	@PreAuthenticateContentAndVerifyIntent(secret = "Kslk30SNF2AChs2", callback = "O/authTypeCallback", topic = "AUTH_TYPE_STATUS_UPDATE")
	public void updateAuthtypeStatus(IDAEventsDTO events)
			throws IdAuthenticationAppException, IDDataValidationException {
		logger.debug(IdAuthCommonConstants.SESSION_ID, "updateAuthtypeStatus", "", "Inside authTypeCallback");
//		try {
//			List<IDAEventDTO> eventsList = events.getEvents();
//			for (IDAEventDTO event : eventsList) {
//				authtypeStatusService.updateAuthTypeStatus(event.getTokenId(), event.getAuthTypeStatusList());
//
//				auditHelper.audit(AuditModules.AUTH_TYPE_STATUS, AuditEvents.UPDATE_AUTH_TYPE_STATUS_REQUEST_RESPONSE,
//						event.getTokenId(), IdType.UIN, "internal auth type status update status : " + true);
//			}
//		} catch (IdAuthenticationBusinessException e) {
//			logger.error(IdAuthCommonConstants.SESSION_ID, e.getClass().toString(), e.getErrorCode(), e.getErrorText());
//			auditHelper.audit(AuditModules.AUTH_TYPE_STATUS, AuditEvents.UPDATE_AUTH_TYPE_STATUS_REQUEST_RESPONSE,
//					events.getEvents().get(0).getTokenId(), IdType.UIN, e);
//			throw new IdAuthenticationAppException(e.getErrorCode(), e.getErrorText(), e);
//		}

	}

}
