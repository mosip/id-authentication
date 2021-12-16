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
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.idevent.service.IdChangeEventHandlerService;
import io.mosip.authentication.core.util.DataValidationUtil;
import io.mosip.authentication.internal.service.validator.CredentialIssueEventValidator;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.websub.model.EventModel;
import io.mosip.kernel.websub.api.annotation.PreAuthenticateContentAndVerifyIntent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import springfox.documentation.annotations.ApiIgnore;

/**
 * The {@code CredentialIssueanceCallbackController} used to handle the
 * notification events posted by ID Repo module.
 *
 * @author Loganathan Sekar
 */
@RestController
@Tag(name = "credential-issueance-callback-controller", description = "Credential Issueance Callback Controller")
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
	@Operation(summary = "Event Notification Callback API", description = "Event Notification Callback API", tags = { "credential-issueance-callback-controller" })
	
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Request authenticated successfully",
					content = @Content(array = @ArraySchema(schema = @Schema(implementation = IdAuthenticationAppException.class)))),
			@ApiResponse(responseCode = "201", description = "Created" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "401", description = "Unauthorized" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "403", description = "Forbidden" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "404", description = "Not Found" ,content = @Content(schema = @Schema(hidden = true)))})
	@PreAuthenticateContentAndVerifyIntent(secret = "${"+ IDA_WEBSUB_CRED_ISSUE_CALLBACK_SECRET +"}",callback = "${ida-websub-idchange-credential-issued-callback-relative-url}" ,topic = "${ida-topic-credential-issued}")
	public ResponseWrapper<?> handleCredentialIssuedEvent(@PathVariable("partnerId") String partnerId, 
			@Validated @RequestBody EventModel eventModel, @ApiIgnore Errors e) throws IdAuthenticationBusinessException {
		logger.debug(IdAuthCommonConstants.SESSION_ID, "handleCredentialIssuedEvent",  this.getClass().getCanonicalName(), "inside credentialIssueanceCallback for partnerId: " + partnerId);
		return handleEvent(eventModel, e);
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
	@Operation(summary = "Event Notification Callback API", description = "Event Notification Callback API", tags = { "credential-issueance-callback-controller" })
	
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Request authenticated successfully",
					content = @Content(array = @ArraySchema(schema = @Schema(implementation = IdAuthenticationAppException.class)))),
			@ApiResponse(responseCode = "201", description = "Created" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "401", description = "Unauthorized" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "403", description = "Forbidden" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "404", description = "Not Found" ,content = @Content(schema = @Schema(hidden = true)))})
	@PreAuthenticateContentAndVerifyIntent(secret = "${"+ IDA_WEBSUB_CRED_ISSUE_CALLBACK_SECRET +"}",callback = "${ida-websub-idchange-remove-id-callback-relative-url}",topic = "${ida-topic-remove-id}")
	public ResponseWrapper<?> handleRemoveIdEvent(@PathVariable("partnerId") String partnerId, 
			@Validated @RequestBody EventModel eventModel, @ApiIgnore Errors e) throws IdAuthenticationBusinessException {
		logger.debug(IdAuthCommonConstants.SESSION_ID, "handleRemoveIdEvent", this.getClass().getCanonicalName(), "inside credentialIssueanceCallback for partnerId: " + partnerId);
		return handleEvent(eventModel, e);
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
	@Operation(summary = "Event Notification Callback API", description = "Event Notification Callback API", tags = { "credential-issueance-callback-controller" })
	
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Request authenticated successfully",
					content = @Content(array = @ArraySchema(schema = @Schema(implementation = IdAuthenticationAppException.class)))),
			@ApiResponse(responseCode = "201", description = "Created" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "401", description = "Unauthorized" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "403", description = "Forbidden" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "404", description = "Not Found" ,content = @Content(schema = @Schema(hidden = true)))})
	@PreAuthenticateContentAndVerifyIntent(secret = "${"+ IDA_WEBSUB_CRED_ISSUE_CALLBACK_SECRET +"}",callback = "${ida-websub-idchange-deactivate-id-callback-relative-url}",topic = "${ida-topic-deactivate-id}")
	public ResponseWrapper<?> handleDeactivateIdEvent(@PathVariable("partnerId") String partnerId, 
			@Validated @RequestBody EventModel eventModel, @ApiIgnore Errors e) throws IdAuthenticationBusinessException {
		logger.debug(IdAuthCommonConstants.SESSION_ID, "handleDeactivateIdEvent",  this.getClass().getCanonicalName(), "inside credentialIssueanceCallback for partnerId: " + partnerId);
		return handleEvent(eventModel, e);
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
	@Operation(summary = "Event Notification Callback API", description = "Event Notification Callback API", tags = { "credential-issueance-callback-controller" })
	
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Request authenticated successfully",
					content = @Content(array = @ArraySchema(schema = @Schema(implementation = IdAuthenticationAppException.class)))),
			@ApiResponse(responseCode = "201", description = "Created" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "401", description = "Unauthorized" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "403", description = "Forbidden" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "404", description = "Not Found" ,content = @Content(schema = @Schema(hidden = true)))})
	@PreAuthenticateContentAndVerifyIntent(secret = "${"+ IDA_WEBSUB_CRED_ISSUE_CALLBACK_SECRET +"}",callback = "${ida-websub-idchange-activate-id-callback-relative-url}",topic = "${ida-topic-activate-id}")
	public ResponseWrapper<?> handleActivateIdEvent(@PathVariable("partnerId") String partnerId, 
			@Validated @RequestBody EventModel eventModel, @ApiIgnore Errors e) throws IdAuthenticationBusinessException {
		logger.debug(IdAuthCommonConstants.SESSION_ID, "handleActivateIdEvent",  this.getClass().getCanonicalName(), "inside credentialIssueanceCallback for partnerId: " + partnerId);
		return handleEvent(eventModel, e);
	}

	private ResponseWrapper<?> handleEvent(EventModel eventModel, Errors e)
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
