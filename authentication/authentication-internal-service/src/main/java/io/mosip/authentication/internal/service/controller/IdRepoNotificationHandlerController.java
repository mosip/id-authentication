package io.mosip.authentication.internal.service.controller;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.authentication.common.service.integration.PartnerServiceManager;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.dto.DataValidationUtil;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.idevent.service.IdChangeEventHandlerService;
import io.mosip.authentication.internal.service.validator.IdEventNotificationValidator;
import io.mosip.idrepository.core.constant.IDAEventType;
import io.mosip.idrepository.core.dto.EventModel;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.websub.spi.PublisherClient;
import io.mosip.kernel.core.websub.spi.SubscriptionClient;
import io.mosip.kernel.websub.api.annotation.PreAuthenticateContentAndVerifyIntent;
import io.mosip.kernel.websub.api.model.SubscriptionChangeRequest;
import io.mosip.kernel.websub.api.model.SubscriptionChangeResponse;
import io.mosip.kernel.websub.api.model.UnsubscriptionRequest;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import springfox.documentation.annotations.ApiIgnore;

/**
 * The {@code IdRepoNotificationHandlerController} used to handle the
 * notification events posted by ID Repo module.
 *
 * @author Loganathan Sekar
 */
@RestController
public class IdRepoNotificationHandlerController {
	
	private static Logger logger = IdaLogger.getLogger(IdRepoNotificationHandlerController.class);
	
	/** The id change event handler service. */
	@Autowired
	private IdChangeEventHandlerService idChangeEventHandlerService;

	/** The validator. */
	@Autowired
	private IdEventNotificationValidator validator;
	
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
	private PartnerServiceManager partnerServiceManager;
	
	@Autowired
	SubscriptionClient<SubscriptionChangeRequest, UnsubscriptionRequest, SubscriptionChangeResponse> subscribe; 
	
	@Autowired
	private PublisherClient<String, EventModel, HttpHeaders> publisher; 

	/**
	 * Inits the binder.
	 *
	 * @param binder the binder
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(validator);
	}
	
	@PostConstruct
	public void postConstrcut() {
		List<String> partnerIds = partnerServiceManager.getPartnerIds();
		tryRegisterTopicCredentialIssueanceEvents(partnerIds);
		subscribeForCredentialIssueanceEvents(partnerIds);
	}
	
	private void tryRegisterTopicCredentialIssueanceEvents(List<String> partnerIds) {
		partnerIds.forEach(partnerId -> {
			
			Arrays.stream(IDAEventType.values()).forEach(eventType -> {
				String topic = partnerId + "/" + eventType.toString();
				try {
					publisher.registerTopic(topic, publisherUrl);
				} catch (Exception e) {
					logger.info(IdAuthCommonConstants.SESSION_ID, "tryRegisterTopicCredentialIssueanceEvents",  e.getClass().toString(), "Error subscribing topic: "+ topic +"\n" + e.getMessage());
				}
			});
			
		});
	}

	private void subscribeForCredentialIssueanceEvents(List<String> partnerIds) {
				partnerIds.forEach(partnerId -> {
					
					Arrays.stream(IDAEventType.values()).forEach(eventType -> {
						String topic = partnerId + "/" + eventType.toString();
						try {
							SubscriptionChangeRequest subscriptionRequest = new SubscriptionChangeRequest();
							subscriptionRequest.setCallbackURL(credentialIssueCallbackURL);
							subscriptionRequest.setHubURL(hubURL);
							subscriptionRequest.setSecret(secret);
							subscriptionRequest.setTopic(topic);
							logger.info(IdAuthCommonConstants.SESSION_ID, "subscribeForCredentialIssueanceEvents", "", "Trying to register topic: " + topic);
							subscribe.subscribe(subscriptionRequest);
						} catch (Exception e) {
							logger.info(IdAuthCommonConstants.SESSION_ID, "subscribeForCredentialIssueanceEvents",  e.getClass().toString(), "Error subscribing topic: "+ topic +"\n" + e.getMessage());
							throw e;
						}
					});
					
				});
	}
	
	/**
	 * Handle events end point.
	 *
	 * @param notificationEventsDto the notification events dto
	 * @param e the e
	 * @return the response entity
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	@PreAuthorize("hasAnyRole('REGISTRATION_PROCESSOR', 'RESIDENT', 'ID_AUTHENTICATION')")
	@PostMapping(path = "/credentialIssueanceCallback", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Event Notification Callback API", response = IdAuthenticationAppException.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Request authenticated successfully") })
	@PreAuthenticateContentAndVerifyIntent(secret = "Kslk30SNF2AChs2",callback = "/credentialIssueanceCallback",topic = "*/CREDENTIAL_ISSUED")
	public ResponseWrapper<?> handleEvents(@Validated @RequestBody EventModel eventModel, @ApiIgnore Errors e) throws IdAuthenticationBusinessException {
		DataValidationUtil.validate(e);
		handleEvents(eventModel);
		return new ResponseWrapper<>();
	}

	/**
	 * Handle events.
	 *
	 * @param events the events
	 * @return true, if successful
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	private void handleEvents(EventModel events) throws IdAuthenticationBusinessException {
		idChangeEventHandlerService.handleIdEvent(events);
	}

}
