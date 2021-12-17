package io.mosip.authentication.service.controller;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.CA_CERT_EVENT;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_CA_CERT_CALLBACK_SECRET;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_CA_CERT_TOPIC;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.authentication.common.service.spi.websub.PartnerCACertEventService;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.idrepository.core.exception.RestServiceException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.websub.model.EventModel;
import io.mosip.kernel.websub.api.annotation.PreAuthenticateContentAndVerifyIntent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @author Manoj SP
 *
 */
@RestController
@Tag(name = "partner-ca-cert-event-controller", description = "Partner CA Cert Event Controller")
public class PartnerCACertEventController {
	
	private static Logger logger = IdaLogger.getLogger(PartnerCACertEventController.class);
	
	@Autowired
	private PartnerCACertEventService partnerCACertEventService;

	@PostMapping(value = "/callback/partnermanagement/" + CA_CERT_EVENT, consumes = "application/json")
	@Operation(summary = "handleCACertificate", description = "handleCACertificate", tags = { "partner-ca-cert-event-controller" })
	
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "201", description = "Created" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "401", description = "Unauthorized" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "403", description = "Forbidden" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "404", description = "Not Found" ,content = @Content(schema = @Schema(hidden = true)))})
	@PreAuthenticateContentAndVerifyIntent(secret = "${" + IDA_WEBSUB_CA_CERT_CALLBACK_SECRET
			+ "}", callback = "${ida-websub-ca-cert-callback-relative-url}", topic = "${" + IDA_WEBSUB_CA_CERT_TOPIC + "}")
	public void handleCACertificate(@RequestBody EventModel eventModel) throws RestServiceException, IdAuthenticationBusinessException {
		logger.debug(IdAuthCommonConstants.SESSION_ID, "PartnerCACertEventController", "handleCACertificate", "EVENT RECEIVED");
		// Evict the cache for the partner domain present in the event so that it will
		// be re-cached with new certificates
		partnerCACertEventService.evictCACertCache(eventModel);
	}

}
