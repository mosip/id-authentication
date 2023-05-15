package io.mosip.authentication.internal.service.controller;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.APIKEY_APPROVED;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.MISP_LICENSE_GENERATED;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.MISP_LICENSE_UPDATED;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.PARTNER_API_KEY_UPDATED_EVENT_NAME;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.PARTNER_UPDATED_EVENT_NAME;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.POLICY_UPDATED_EVENT_NAME;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.OIDC_CLIENT_CREATED;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.OIDC_CLIENT_UPDATED;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_PARTNER_SERVICE_CALLBACK_SECRET;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_TOPIC_PMP_MISP_LICENSE_GENERATED;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_TOPIC_PMP_MISP_LICENSE_UPDATED;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_TOPIC_PMP_OIDC_CLIENT_CREATED;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_TOPIC_PMP_OIDC_CLIENT_UPDATED;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_TOPIC_PMP_PARTNER_API_KEY_APPROVED;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_TOPIC_PMP_PARTNER_API_KEY_UPDATED;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_TOPIC_PMP_PARTNER_UPDATED;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_TOPIC_PMP_POLICY_UPDATED;


import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.authentication.common.service.integration.PartnerServiceManager;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.websub.model.EventModel;
import io.mosip.kernel.websub.api.annotation.PreAuthenticateContentAndVerifyIntent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * The Class PartnerServiceCallbackController.
 * 
 * @author Manoj SP
 */
@RestController
@Tag(name = "partner-service-callback-controller", description = "Partner Service Callback Controller")
public class PartnerServiceCallbackController {

	private static final Logger logger = IdaLogger.getLogger(PartnerServiceCallbackController.class);

	/** The controller delegate. */
	@Autowired
	private PartnerServiceManager partnerManager;

	@Autowired
	private IdAuthSecurityManager securityManager;

	@PostMapping(value = "/callback/partnermanagement/" + APIKEY_APPROVED, consumes = "application/json")
	@Operation(summary = "handleApiKeyApprovedEvent", description = "handleApiKeyApprovedEvent", tags = { "partner-service-callback-controller" })
	
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "201", description = "Created" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "401", description = "Unauthorized" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "403", description = "Forbidden" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "404", description = "Not Found" ,content = @Content(schema = @Schema(hidden = true)))})
	@PreAuthenticateContentAndVerifyIntent(secret = "${" + IDA_WEBSUB_PARTNER_SERVICE_CALLBACK_SECRET
			+ "}", callback = "${ida-websub-partner-service-apikey-approved-callback-relative-url}", topic = "${" + IDA_WEBSUB_TOPIC_PMP_PARTNER_API_KEY_APPROVED + "}")
	public void handleApiKeyApprovedEvent(@RequestBody EventModel eventModel) {
		try {
			logger.debug(securityManager.getUser(), "PartnerServiceCallbackController", "handleApiKeyApprovedEvent",
					APIKEY_APPROVED + " EVENT RECEIVED");
			partnerManager.handleApiKeyApproved(eventModel);
		} catch (Exception e) {
			logger.error(securityManager.getUser(), "PartnerServiceCallbackController", "handleApiKeyApprovedEvent",
					ExceptionUtils.getFullStackTrace(e));
		}
	}

	@PostMapping(value = "/callback/partnermanagement/" + PARTNER_UPDATED_EVENT_NAME, consumes = "application/json")
	@Operation(summary = "handlePartnerApiKeyUpdated", description = "handlePartnerApiKeyUpdated", tags = { "partner-service-callback-controller" })
	
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "201", description = "Created" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "401", description = "Unauthorized" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "403", description = "Forbidden" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "404", description = "Not Found" ,content = @Content(schema = @Schema(hidden = true)))})
	@PreAuthenticateContentAndVerifyIntent(secret = "${" + IDA_WEBSUB_PARTNER_SERVICE_CALLBACK_SECRET
			+ "}", callback = "${ida-websub-partner-service-partner-updated-callback-relative-url}", topic = "${" + IDA_WEBSUB_TOPIC_PMP_PARTNER_UPDATED + "}")
	public void handlePartnerUpdated(@RequestBody EventModel eventModel) {
		try {
			logger.debug(securityManager.getUser(), "PartnerServiceCallbackController", "handlePartnerUpdated",
					PARTNER_UPDATED_EVENT_NAME + " EVENT RECEIVED");
			partnerManager.updatePartnerData(eventModel);
		} catch (Exception e) {
			logger.error(securityManager.getUser(), "PartnerServiceCallbackController", "handlePartnerUpdated",
					ExceptionUtils.getFullStackTrace(e));
		}
	}

	@PostMapping(value = "/callback/partnermanagement/" + POLICY_UPDATED_EVENT_NAME, consumes = "application/json")
	@Operation(summary = "handlePolicyUpdated", description = "handlePolicyUpdated", tags = { "partner-service-callback-controller" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "201", description = "Created" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "401", description = "Unauthorized" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "403", description = "Forbidden" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "404", description = "Not Found" ,content = @Content(schema = @Schema(hidden = true)))})
	@PreAuthenticateContentAndVerifyIntent(secret = "${" + IDA_WEBSUB_PARTNER_SERVICE_CALLBACK_SECRET
			+ "}", callback = "${ida-websub-partner-service-policy-updated-callback-relative-url}", topic = "${" + IDA_WEBSUB_TOPIC_PMP_POLICY_UPDATED + "}")
	public void handlePolicyUpdated(@RequestBody EventModel eventModel) {
		try {
			logger.debug(securityManager.getUser(), "PartnerServiceCallbackController", "handlePolicyUpdated",
					POLICY_UPDATED_EVENT_NAME + " EVENT RECEIVED");
			partnerManager.updatePolicyData(eventModel);
		} catch (Exception e) {
			logger.error(securityManager.getUser(), "PartnerServiceCallbackController", "handlePolicyUpdated",
					ExceptionUtils.getFullStackTrace(e));
		}
	}

	@PostMapping(value = "/callback/partnermanagement/" + PARTNER_API_KEY_UPDATED_EVENT_NAME, consumes = "application/json")
	@Operation(summary = "handlePartnerApiKeyUpdated", description = "handlePartnerApiKeyUpdated", tags = { "partner-service-callback-controller" })
	
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "201", description = "Created" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "401", description = "Unauthorized" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "403", description = "Forbidden" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "404", description = "Not Found" ,content = @Content(schema = @Schema(hidden = true)))})
	@PreAuthenticateContentAndVerifyIntent(secret = "${" + IDA_WEBSUB_PARTNER_SERVICE_CALLBACK_SECRET
			+ "}", callback = "${ida-websub-partner-service-partner-api-key-updated-callback-relative-url}", topic = "${" + IDA_WEBSUB_TOPIC_PMP_PARTNER_API_KEY_UPDATED + "}")
	public void handlePartnerApiKeyUpdated(@RequestBody EventModel eventModel) {
		try {
			logger.debug(securityManager.getUser(), "PartnerServiceCallbackController", "handlePartnerApiKeyUpdated",
					PARTNER_API_KEY_UPDATED_EVENT_NAME + " EVENT RECEIVED");
			partnerManager.handleApiKeyUpdated(eventModel);
		} catch (Exception e) {
			logger.error(securityManager.getUser(), "PartnerServiceCallbackController", "handlePartnerApiKeyUpdated",
					ExceptionUtils.getFullStackTrace(e));
		}
	}

	@PostMapping(value = "/callback/partnermanagement/" + MISP_LICENSE_GENERATED, consumes = "application/json")
	@Operation(summary = "handleMispLicenseGeneratedEvent", description = "handleMispLicenseGeneratedEvent", tags = { "partner-service-callback-controller" })
	
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "201", description = "Created" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "401", description = "Unauthorized" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "403", description = "Forbidden" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "404", description = "Not Found" ,content = @Content(schema = @Schema(hidden = true)))})
	@PreAuthenticateContentAndVerifyIntent(secret = "${" + IDA_WEBSUB_PARTNER_SERVICE_CALLBACK_SECRET
			+ "}", callback = "${ida-websub-partner-service-misp-license-generated-callback-relative-url}", topic = "${" + IDA_WEBSUB_TOPIC_PMP_MISP_LICENSE_GENERATED + "}")
	public void handleMispLicenseGeneratedEvent(@RequestBody EventModel eventModel) {
		try {
			logger.debug(securityManager.getUser(), "PartnerServiceCallbackController", "handleMispLicenseGeneratedEvent",
					MISP_LICENSE_GENERATED + " EVENT RECEIVED");
			partnerManager.updateMispLicenseData(eventModel);
		} catch (Exception e) {
			logger.error(securityManager.getUser(), "PartnerServiceCallbackController", "handleMispLicenseGeneratedEvent",
					ExceptionUtils.getFullStackTrace(e));
		}
	}

	@PostMapping(value = "/callback/partnermanagement/" + MISP_LICENSE_UPDATED, consumes = "application/json")
	@Operation(summary = "handleMispUpdatedEvent", description = "handleMispUpdatedEvent", tags = { "partner-service-callback-controller" })
	
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "201", description = "Created" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "401", description = "Unauthorized" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "403", description = "Forbidden" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "404", description = "Not Found" ,content = @Content(schema = @Schema(hidden = true)))})
	@PreAuthenticateContentAndVerifyIntent(secret = "${" + IDA_WEBSUB_PARTNER_SERVICE_CALLBACK_SECRET
			+ "}", callback = "${ida-websub-partner-service-misp-license-updated-callback-relative-url}", topic = "${" + IDA_WEBSUB_TOPIC_PMP_MISP_LICENSE_UPDATED + "}")
	public void handleMispUpdatedEvent(@RequestBody EventModel eventModel) {
		try {
			logger.debug(securityManager.getUser(), "PartnerServiceCallbackController", "handleMispUpdatedEvent",
					MISP_LICENSE_UPDATED + " EVENT RECEIVED");
			partnerManager.updateMispLicenseData(eventModel);
		} catch (Exception e) {
			logger.error(securityManager.getUser(), "PartnerServiceCallbackController", "handleMispUpdatedEvent",
					ExceptionUtils.getFullStackTrace(e));
		}
	}


	@PostMapping(value = "/callback/partnermanagement/" + OIDC_CLIENT_CREATED, consumes = "application/json")
	@Operation(summary = "handleOIDCClientCreatedEvent", description = "TO Handle OIDC Client Created Event", tags = { "partner-service-callback-controller" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "201", description = "Created" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "401", description = "Unauthorized" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "403", description = "Forbidden" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "404", description = "Not Found" ,content = @Content(schema = @Schema(hidden = true)))})
	@PreAuthenticateContentAndVerifyIntent(secret = "${" + IDA_WEBSUB_PARTNER_SERVICE_CALLBACK_SECRET
			+ "}", callback = "${ida-websub-partner-service-oidc-client-created-callback-relative-url}", topic = "${" + IDA_WEBSUB_TOPIC_PMP_OIDC_CLIENT_CREATED + "}")
	public void handleOIDCClientCreatedEvent(@RequestBody EventModel eventModel) {
		try {
			logger.debug(securityManager.getUser(), "PartnerServiceCallbackController", "handleOIDCClientCreatedEvent",
						OIDC_CLIENT_CREATED + " EVENT RECEIVED");
			partnerManager.createOIDCClientData(eventModel);
		} catch (Exception e) {
			logger.error(securityManager.getUser(), "PartnerServiceCallbackController", "handleMispUpdatedEvent",
					ExceptionUtils.getFullStackTrace(e));
		}
	}


	@PostMapping(value = "/callback/partnermanagement/" + OIDC_CLIENT_UPDATED, consumes = "application/json")
	@Operation(summary = "handleOIDCClientUpdatedEvent", description = "TO Handle OIDC Client Updated Event", tags = { "partner-service-callback-controller" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "201", description = "Created" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "401", description = "Unauthorized" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "403", description = "Forbidden" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "404", description = "Not Found" ,content = @Content(schema = @Schema(hidden = true)))})
	@PreAuthenticateContentAndVerifyIntent(secret = "${" + IDA_WEBSUB_PARTNER_SERVICE_CALLBACK_SECRET
			+ "}", callback = "${ida-websub-partner-service-oidc-client-updated-callback-relative-url}", topic = "${" + IDA_WEBSUB_TOPIC_PMP_OIDC_CLIENT_UPDATED + "}")
	public void handleOIDCClientUpdatedEvent(@RequestBody EventModel eventModel) {
		try {
			logger.debug(securityManager.getUser(), "PartnerServiceCallbackController", "handleOIDCClientUpdatedEvent",
						OIDC_CLIENT_UPDATED + " EVENT RECEIVED");
			partnerManager.updateOIDCClientData(eventModel);
		} catch (Exception e) {
			logger.error(securityManager.getUser(), "PartnerServiceCallbackController", "handleMispUpdatedEvent",
					ExceptionUtils.getFullStackTrace(e));
		}
	}
}