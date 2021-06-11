package io.mosip.authentication.internal.service.controller;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.CA_CERT_EVENT;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_CA_CERT_CALLBACK_SECRET;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_CA_CERT_TOPIC;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.authentication.common.service.impl.patrner.PartnerCACertEventService;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.websub.model.EventModel;
import io.mosip.kernel.websub.api.annotation.PreAuthenticateContentAndVerifyIntent;

/**
 * @author Manoj SP
 *
 */
@RestController
public class PartnerCACertEventController {
	
	private static Logger logger = IdaLogger.getLogger(PartnerCACertEventController.class);
	
	@Autowired
	private PartnerCACertEventService partnerCACertEventService;

	@PostMapping(value = "/callback/partnermanagement/" + CA_CERT_EVENT, consumes = "application/json")
	@PreAuthenticateContentAndVerifyIntent(secret = "${" + IDA_WEBSUB_CA_CERT_CALLBACK_SECRET
			+ "}", callback = "/idauthentication/v1/internal/callback/partnermanagement/" + CA_CERT_EVENT, topic = "${" + IDA_WEBSUB_CA_CERT_TOPIC + "}")
	public void handleCACertificate(@RequestBody EventModel eventModel) throws RestServiceException, IdAuthenticationBusinessException {
		logger.debug(IdAuthCommonConstants.SESSION_ID, "PartnerCACertEventController", "handleCACertificate", "EVENT RECEIVED");
		partnerCACertEventService.handleCACertEvent(eventModel);
	}

}
