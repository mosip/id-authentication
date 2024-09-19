package io.mosip.authentication.internal.service.controller;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_CRED_ISSUE_CALLBACK_SECRET;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

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

	@GetMapping(path = "/callback/idchange/activate_id/{partnerId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Event Notification Callback API - Verification", description = "Handles subscription verification from the WebSub hub", tags = { "credential-issueance-callback-controller" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Verification successful",
					content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(hidden = true)))
	})
	public ResponseEntity<String> verifySubscription(
			@PathVariable("partnerId") String partnerId,
			@RequestParam("hub.mode") String mode,
			@RequestParam("hub.topic") String topic,
			@RequestParam("hub.challenge") String challenge,
			@RequestParam(value = "hub.lease_seconds", required = false) String leaseSeconds) {

		logger.debug(IdAuthCommonConstants.SESSION_ID, "verifySubscription", this.getClass().getCanonicalName(),
				"inside subscription verification for partnerId: " + partnerId);
		System.out.println("inside verify subscription");
		// Check if the mode is "subscribe" or "unsubscribe"
		if ("subscribe".equalsIgnoreCase(mode)) {
			// Respond with the hub.challenge to confirm the subscription
			return ResponseEntity.ok(challenge);
		} else if ("unsubscribe".equalsIgnoreCase(mode)) {
			// Handle unsubscription if necessary
			return ResponseEntity.ok(challenge);
		} else {
			// If the mode is not recognized, return a bad request response
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid mode");
		}
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
		System.out.println("inside post mapping handleActivateeventId");
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
