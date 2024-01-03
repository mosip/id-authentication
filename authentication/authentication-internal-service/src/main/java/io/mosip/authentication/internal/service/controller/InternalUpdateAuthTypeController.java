package io.mosip.authentication.internal.service.controller;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_AUTHTYPE_CALLBACK_SECRET;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.authtype.status.service.UpdateAuthtypeStatusService;
import io.mosip.idrepository.core.dto.AuthTypeStatusEventDTO;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.websub.model.EventModel;
import io.mosip.kernel.core.websub.spi.SubscriptionClient;
import io.mosip.kernel.websub.api.annotation.PreAuthenticateContentAndVerifyIntent;
import io.mosip.kernel.websub.api.model.SubscriptionChangeRequest;
import io.mosip.kernel.websub.api.model.SubscriptionChangeResponse;
import io.mosip.kernel.websub.api.model.UnsubscriptionRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * The InternalUpdateAuthTypeController use to fetch Auth Transaction.
 *
 * @author Dinesh Karuppiah.T
 */
@RestController
@Tag(name = "internal-update-auth-type-controller", description = "Internal Update Auth Type Controller")
public class InternalUpdateAuthTypeController {

	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(InternalUpdateAuthTypeController.class);

	@Autowired
	private UpdateAuthtypeStatusService authtypeStatusService;

	@Autowired
	private AuditHelper auditHelper;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	@Qualifier("subscriptionExtendedClient")
	SubscriptionClient<SubscriptionChangeRequest, UnsubscriptionRequest, SubscriptionChangeResponse> subscribe;

	@PostMapping(value = "/callback/authTypeCallback/{partnerId}", consumes = "application/json")
	@Operation(summary = "updateAuthtypeStatus", description = "updateAuthtypeStatus", tags = { "internal-update-auth-type-controller" })
	
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "201", description = "Created" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "401", description = "Unauthorized" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "403", description = "Forbidden" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "404", description = "Not Found" ,content = @Content(schema = @Schema(hidden = true)))})
	@PreAuthenticateContentAndVerifyIntent(secret = "${"+ IDA_WEBSUB_AUTHTYPE_CALLBACK_SECRET +"}", callback = "${ida-websub-auth-type-callback-relative-url}", topic = "${ida-topic-auth-type-status-updated}")
	public void updateAuthtypeStatus(@RequestBody EventModel eventModel, @PathVariable("partnerId") String partnerId)
			throws IdAuthenticationAppException, IDDataValidationException {
		if(eventModel.getEvent() != null && eventModel.getEvent().getData() != null) {
			AuthTypeStatusEventDTO event = mapper.convertValue(eventModel.getEvent().getData(), AuthTypeStatusEventDTO.class);
			try {
					logger.debug(IdAuthCommonConstants.SESSION_ID, "updateAuthtypeStatus", this.getClass().getCanonicalName(), "handling updateAuthtypeStatus event for partnerId: " + partnerId);
	
					authtypeStatusService.updateAuthTypeStatus(event.getTokenId(), event.getAuthTypeStatusList());
	
					auditHelper.audit(AuditModules.AUTH_TYPE_STATUS, AuditEvents.UPDATE_AUTH_TYPE_STATUS_REQUEST_RESPONSE,
							eventModel.getEvent().getId(), IdType.UIN, "internal auth type status update status : " + true);
			} catch (IdAuthenticationBusinessException e) {
				logger.error(IdAuthCommonConstants.SESSION_ID, e.getClass().toString(), e.getErrorCode(), e.getErrorText());
				auditHelper.audit(AuditModules.AUTH_TYPE_STATUS, AuditEvents.UPDATE_AUTH_TYPE_STATUS_REQUEST_RESPONSE,
						eventModel.getEvent().getId(), IdType.UIN, e);
				throw new IdAuthenticationAppException(e.getErrorCode(), e.getErrorText(), e);
			}
		}

	}

}
