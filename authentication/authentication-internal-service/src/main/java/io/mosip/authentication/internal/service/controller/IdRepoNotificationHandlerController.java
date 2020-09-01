package io.mosip.authentication.internal.service.controller;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_SECRET;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.ID_REPO_WEBSUB_HUB_URL;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import io.mosip.authentication.core.dto.DataValidationUtil;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.idevent.service.IdChangeEventHandlerService;
import io.mosip.authentication.internal.service.validator.IdEventNotificationValidator;
import io.mosip.idrepository.core.constant.IDAEventType;
import io.mosip.idrepository.core.dto.EventModel;
import io.mosip.kernel.core.http.ResponseWrapper;
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
	
	/** The id change event handler service. */
	@Autowired
	private IdChangeEventHandlerService idChangeEventHandlerService;

	/** The validator. */
	@Autowired
	private IdEventNotificationValidator validator;
	
	@Autowired
	SubscriptionClient<SubscriptionChangeRequest,UnsubscriptionRequest, SubscriptionChangeResponse> sb; 
			
	@Value("${" + ID_REPO_WEBSUB_HUB_URL + "}")
	private String webSubHubUrl;
	
	
	@Value("${" + IDA_WEBSUB_SECRET + "}")
	private String webSubSecret;
	
	@Autowired
	private PartnerServiceManager partnerServiceManager;

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
	public void postConstruct() {
		List<String> partnerIds = partnerServiceManager.getPartnerIds();
		
		partnerIds.forEach(partnerId -> {
			
			Arrays.stream(IDAEventType.values()).forEach(eventType -> {
				SubscriptionChangeRequest subscriptionRequest = new SubscriptionChangeRequest();
				subscriptionRequest.setCallbackURL("/notify");
				subscriptionRequest.setHubURL(webSubHubUrl);
				subscriptionRequest.setSecret(webSubSecret);
				subscriptionRequest.setTopic(partnerId + "/" + eventType.toString());
				sb.subscribe(subscriptionRequest);
			});
			
		});
		//TODO Unsubscribe
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
	@PostMapping(path = "/notify", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Event Notification Callback API", response = IdAuthenticationAppException.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Request authenticated successfully") })
	@PreAuthenticateContentAndVerifyIntent(secret = "${ida-websub-secret}",callback = "/notify",topic = "*/CREDENTIAL_ISSUED")
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
