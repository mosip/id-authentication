package io.mosip.authentication.internal.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.dto.DataValidationUtil;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.idevent.service.CredentialStoreService;
import io.mosip.authentication.internal.service.validator.CredentialIssueEventValidator;
import io.mosip.idrepository.core.dto.EventModel;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.websub.api.annotation.PreAuthenticateContentAndVerifyIntent;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import springfox.documentation.annotations.ApiIgnore;

/**
 * The {@code CredentialIssueanceCallbackController} used to handle the
 * notification events posted by ID Repo module.
 *
 * @author Loganathan Sekar
 */
@RestController
public class CredentialIssueanceCallbackController {
	
	private static Logger logger = IdaLogger.getLogger(CredentialIssueanceCallbackController.class);
	
	/** The id change event handler service. */
	@Autowired
	private CredentialStoreService credentialStoreService;

	/** The validator. */
	@Autowired
	private CredentialIssueEventValidator validator;
	
	/**
	 * Inits the binder.
	 *
	 * @param binder the binder
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(validator);
	}
	
	/**
	 * Temporary intent verifier to support path params in callback
	 * 
	 * @param partnerId
	 * @param eventType
	 * @param intentMode
	 * @param mode
	 * @param topic
	 * @param challenge
	 * @param leaseSecs
	 * @return
	 * @throws IdAuthenticationBusinessException
	 */
	@GetMapping(path = "/callback/idchange/{partnerId}/{eventType}")
	@ApiOperation(value = "Event Notification Callback Intent Verifier API", response = IdAuthenticationAppException.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Request authenticated successfully") })
	public ResponseEntity<String> handleEvents(@PathVariable("partnerId") String partnerId, 
			@PathVariable("eventType") String eventType,
			@RequestParam(name = "intentMode", required = false) String intentMode,
			@RequestParam(name = "hub.mode", required = false) String mode,
			@RequestParam(name = "hub.topic", required = false) String topic,
			@RequestParam(name = "hub.challenge", required = false) String challenge,
			@RequestParam(name = "hub.lease_seconds", required = false) String leaseSecs
			) throws IdAuthenticationBusinessException {
		logger.debug(IdAuthCommonConstants.SESSION_ID, "handleEvents", "", "inside intent verification callback for partnerId: " + partnerId 
				+ " \neventType: " + eventType + "\n "
				+ "intentMode: " + intentMode + "\n"
				+ "mode: " + mode + "\n"
				+ "topic: " + topic + "\n"
				+ "challenge: " + challenge + "\n"
				+ "lease_seconds: " + leaseSecs);
		return ResponseEntity.ok().body(challenge == null ? "" : challenge);
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
	@PostMapping(path = "/callback/idchange/{partnerId}/{eventType}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Event Notification Callback API", response = IdAuthenticationAppException.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Request authenticated successfully") })
	@PreAuthenticateContentAndVerifyIntent(secret = "Kslk30SNF2AChs2",callback = "/idauthentication/v1/internal/callback/idchange/*/*",topic = "*/CREDENTIAL_ISSUED")
	public ResponseWrapper<?> handleEvents(@PathVariable("partnerId") String partnerId, 
			@PathVariable("eventType") String eventType,
			@Validated @RequestBody EventModel eventModel, @ApiIgnore Errors e) throws IdAuthenticationBusinessException {
		logger.debug(IdAuthCommonConstants.SESSION_ID, "handleEvents", "", "inside credentialIssueanceCallback for partnerId: " + partnerId);
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
		credentialStoreService.handleIdEvent(events);
	}

}
