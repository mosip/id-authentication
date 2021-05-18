package io.mosip.authentication.kyc.service.controller;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_MASTERDATA_TEMPLATES_CALLBACK_SECRET;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_MASTERDATA_TEMPLATES_CALLBACK_URL;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_MASTERDATA_TITLES_CALLBACK_SECRET;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_MASTERDATA_TITLES_CALLBACK_URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.authentication.common.controller.MasterDataCacheUpdateControllerDelegate;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.websub.model.EventModel;
import io.mosip.kernel.websub.api.annotation.PreAuthenticateContentAndVerifyIntent;

/**
 * The Class MasterDataUpdateEventController.
 *
 * @author Loganathan Sekar
 */
@RestController
public class MasterDataUpdateEventController {
	
	
	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(MasterDataUpdateEventController.class);

	/** The master data cache update controller delegate. */
	@Autowired
	private MasterDataCacheUpdateControllerDelegate masterDataCacheUpdateControllerDelegate;
	
	/**
	 * Handle masterdata templates update.
	 *
	 * @param eventModel the event model
	 */
	@PostMapping(value = "/callback/masterdata/templates", consumes = "application/json")
	@PreAuthenticateContentAndVerifyIntent(secret = "${" + IDA_WEBSUB_MASTERDATA_TEMPLATES_CALLBACK_SECRET
			+ "}", callback = "/idauthentication/v1/kyc/callback/masterdata/templates", topic = "${" + IDA_WEBSUB_MASTERDATA_TEMPLATES_CALLBACK_URL + "}")
	public void handleMasterdataTemplatesUpdate(@RequestBody EventModel eventModel) {
		logger.debug(IdAuthCommonConstants.SESSION_ID, this.getClass().getCanonicalName(), "handleMasterdataTemplatesUpdate", "EVENT RECEIVED");
		masterDataCacheUpdateControllerDelegate.updateTemplates(eventModel);
	}
	
	/**
	 * Handle masterdata titles update.
	 *
	 * @param eventModel the event model
	 */
	@PostMapping(value = "/callback/masterdata/titles", consumes = "application/json")
	@PreAuthenticateContentAndVerifyIntent(secret = "${" + IDA_WEBSUB_MASTERDATA_TITLES_CALLBACK_SECRET
			+ "}", callback = "/idauthentication/v1/kyc/callback/masterdata/titles", topic = "${" + IDA_WEBSUB_MASTERDATA_TITLES_CALLBACK_URL + "}")
	public void handleMasterdataTitlesUpdate(@RequestBody EventModel eventModel) {
		logger.debug(IdAuthCommonConstants.SESSION_ID, this.getClass().getCanonicalName(), "handleMasterdataTitlesUpdate", "EVENT RECEIVED");
		masterDataCacheUpdateControllerDelegate.updateTitles(eventModel);
	}

}
