package io.mosip.authentication.service.controller;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.masterdata.MasterDataCacheUpdateService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.*;

/**
 * The Class MasterDataUpdateEventController.
 *
 * @author Loganathan Sekar
 */
@RestController
@Tag(name = "master-data-update-event-controller", description = "Master Data Update Event Controller")
public class MasterDataUpdateEventController {
	
	
	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(MasterDataUpdateEventController.class);

	/** The master data cache update controller delegate. */
	@Autowired
	private MasterDataCacheUpdateService masterDataCacheUpdateService;
	
	/**
	 * Handle masterdata templates update.
	 *
	 * @param eventModel the event model
	 */
	@PostMapping(value = "/callback/masterdata/templates", consumes = "application/json")
	@Operation(summary = "handleMasterdataTemplatesUpdate", description = "handleMasterdataTemplatesUpdate", tags = { "master-data-update-event-controller" })
	
	@Parameter(in = ParameterIn.HEADER, name = "signature")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "201", description = "Created" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "401", description = "Unauthorized" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "403", description = "Forbidden" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "404", description = "Not Found" ,content = @Content(schema = @Schema(hidden = true)))})
	@PreAuthenticateContentAndVerifyIntent(secret = "${" + IDA_WEBSUB_MASTERDATA_TEMPLATES_CALLBACK_SECRET
			+ "}", callback = "${ida-websub-masterdata-templates-callback-relative-url}", topic = "${" + IDA_WEBSUB_MASTERDATA_TEMPLATES_TOPIC + "}")
	public void handleMasterdataTemplatesUpdate(@RequestBody EventModel eventModel) {
		logger.debug(IdAuthCommonConstants.SESSION_ID, this.getClass().getCanonicalName(), "handleMasterdataTemplatesUpdate", "EVENT RECEIVED");
		masterDataCacheUpdateService.updateTemplates(eventModel);
	}
	
	/**
	 * Handle masterdata titles update.
	 *
	 * @param eventModel the event model
	 */
	@PostMapping(value = "/callback/masterdata/titles", consumes = "application/json")
	@Operation(summary = "handleMasterdataTitlesUpdate", description = "handleMasterdataTitlesUpdate", tags = { "master-data-update-event-controller" })
	
	@Parameter(in = ParameterIn.HEADER, name = "signature")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "201", description = "Created" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "401", description = "Unauthorized" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "403", description = "Forbidden" ,content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = "404", description = "Not Found" ,content = @Content(schema = @Schema(hidden = true)))})
	@PreAuthenticateContentAndVerifyIntent(secret = "${" + IDA_WEBSUB_MASTERDATA_TITLES_CALLBACK_SECRET
			+ "}", callback = "${ida-websub-masterdata-titles-callback-relative-url}", topic = "${" + IDA_WEBSUB_MASTERDATA_TITLES_TOPIC + "}")
	public void handleMasterdataTitlesUpdate(@RequestBody EventModel eventModel) {
		logger.debug(IdAuthCommonConstants.SESSION_ID, this.getClass().getCanonicalName(), "handleMasterdataTitlesUpdate", "EVENT RECEIVED");
		masterDataCacheUpdateService.updateTitles(eventModel);
	}

}
