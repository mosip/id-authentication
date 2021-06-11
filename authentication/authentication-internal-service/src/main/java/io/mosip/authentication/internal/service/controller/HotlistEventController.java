package io.mosip.authentication.internal.service.controller;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_HOTLIST_CALLBACK_SECRET;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_HOTLIST_TOPIC;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.hotlist.service.HotlistService;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.websub.model.EventModel;
import io.mosip.kernel.websub.api.annotation.PreAuthenticateContentAndVerifyIntent;

/**
 * @author Manoj SP
 * @author Mamta A
 */
@RestController
public class HotlistEventController {

	private static Logger logger = IdaLogger.getLogger(HotlistEventController.class);

	@Autowired
	private HotlistService hotlistService;

	@PostMapping(value = "/callback/hotlist", consumes = "application/json")
	@PreAuthenticateContentAndVerifyIntent(secret = "${" + IDA_WEBSUB_HOTLIST_CALLBACK_SECRET
			+ "}", callback = "/idauthentication/v1/internal/callback/hotlist", topic = "${" + IDA_WEBSUB_HOTLIST_TOPIC
					+ "}")
	public void handleHotlisting(@RequestBody EventModel eventModel) throws IdAuthenticationBusinessException {
		logger.debug(IdAuthCommonConstants.SESSION_ID, "HotlistEventController", "handleHotlisting", "EVENT RECEIVED");
		hotlistService.handlingHotlistingEvent(eventModel);
	}

}
