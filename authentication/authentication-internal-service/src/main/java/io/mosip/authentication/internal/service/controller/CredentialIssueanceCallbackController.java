package io.mosip.authentication.internal.service.controller;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_CRED_ISSUE_CALLBACK_SECRET;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.dto.DataValidationUtil;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.idevent.service.IdChangeEventHandlerService;
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
	private IdChangeEventHandlerService credentialStoreService;

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
	 * Handle events end point.
	 *
	 * @param notificationEventsDto the notification events dto
	 * @param e the e
	 * @return the response entity
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	@PostMapping(path = "/callback/idchange/credential_issued/{partnerId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Event Notification Callback API", response = IdAuthenticationAppException.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Request authenticated successfully") })
	//@PreAuthenticateContentAndVerifyIntent(secret = "${"+ IDA_WEBSUB_CRED_ISSUE_CALLBACK_SECRET +"}",callback = "/idauthentication/v1/internal/callback/idchange/credential_issued/{partnerId}",topic = "${ida-topic-credential-issued}")
	public ResponseWrapper<?> handleCredentialIssuedEvent(@PathVariable("partnerId") String partnerId, 
			@Validated @RequestBody EventModel eventModel, @ApiIgnore Errors e) throws IdAuthenticationBusinessException {
		logger.debug(IdAuthCommonConstants.SESSION_ID, "handleCredentialIssuedEvent", "", "inside credentialIssueanceCallback for partnerId: " + partnerId);
		return handleEevent(eventModel, e);
	}
	
	/**
	 * Handle events end point.
	 *
	 * @param notificationEventsDto the notification events dto
	 * @param e the e
	 * @return the response entity
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	@PostMapping(path = "/callback/idchange/remove_id/{partnerId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Event Notification Callback API", response = IdAuthenticationAppException.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Request authenticated successfully") })
	@PreAuthenticateContentAndVerifyIntent(secret = "${"+ IDA_WEBSUB_CRED_ISSUE_CALLBACK_SECRET +"}",callback = "/idauthentication/v1/internal/callback/idchange/remove_id/{partnerId}",topic = "${ida-topic-remove-id}")
	public ResponseWrapper<?> handleRemoveIdEvent(@PathVariable("partnerId") String partnerId, 
			@Validated @RequestBody EventModel eventModel, @ApiIgnore Errors e) throws IdAuthenticationBusinessException {
		logger.debug(IdAuthCommonConstants.SESSION_ID, "handleRemoveIdEvent", "", "inside credentialIssueanceCallback for partnerId: " + partnerId);
		return handleEevent(eventModel, e);
	}
	
	/**
	 * Handle events end point.
	 *
	 * @param notificationEventsDto the notification events dto
	 * @param e the e
	 * @return the response entity
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	@PostMapping(path = "/callback/idchange/deactivate_id/{partnerId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Event Notification Callback API", response = IdAuthenticationAppException.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Request authenticated successfully") })
	@PreAuthenticateContentAndVerifyIntent(secret = "${"+ IDA_WEBSUB_CRED_ISSUE_CALLBACK_SECRET +"}",callback = "/idauthentication/v1/internal/callback/idchange/deactivate_id/{partnerId}",topic = "${ida-topic-deactivate-id}")
	public ResponseWrapper<?> handleDeactivateIdEvent(@PathVariable("partnerId") String partnerId, 
			@Validated @RequestBody EventModel eventModel, @ApiIgnore Errors e) throws IdAuthenticationBusinessException {
		logger.debug(IdAuthCommonConstants.SESSION_ID, "handleEvents", "", "inside credentialIssueanceCallback for partnerId: " + partnerId);
		return handleEevent(eventModel, e);
	}
	
	/**
	 * Handle events end point.
	 *
	 * @param notificationEventsDto the notification events dto
	 * @param e the e
	 * @return the response entity
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	@PostMapping(path = "/callback/idchange/activate_id/{partnerId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Event Notification Callback API", response = IdAuthenticationAppException.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Request authenticated successfully") })
	@PreAuthenticateContentAndVerifyIntent(secret = "${"+ IDA_WEBSUB_CRED_ISSUE_CALLBACK_SECRET +"}",callback = "/idauthentication/v1/internal/callback/idchange/activate_id/{partnerId}",topic = "${ida-topic-activate-id}")
	public ResponseWrapper<?> handleActivateIdEvent(@PathVariable("partnerId") String partnerId, 
			@Validated @RequestBody EventModel eventModel, @ApiIgnore Errors e) throws IdAuthenticationBusinessException {
		logger.debug(IdAuthCommonConstants.SESSION_ID, "handleEvents", "", "inside credentialIssueanceCallback for partnerId: " + partnerId);
		return handleEevent(eventModel, e);
	}

	private ResponseWrapper<?> handleEevent(EventModel eventModel, Errors e)
			throws IDDataValidationException, IdAuthenticationBusinessException {
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
