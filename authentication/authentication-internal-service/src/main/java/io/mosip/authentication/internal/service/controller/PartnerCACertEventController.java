package io.mosip.authentication.internal.service.controller;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.PARTNER_CERT_EVENT;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_PARTNER_CERT_CALLBACK_SECRET;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_PARTNER_CERT_TOPIC;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.idrepository.core.dto.EventModel;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.partnercertservice.dto.CACertificateRequestDto;
import io.mosip.kernel.partnercertservice.service.spi.PartnerCertificateManagerService;
import io.mosip.kernel.websub.api.annotation.PreAuthenticateContentAndVerifyIntent;

/**
 * @author Manoj SP
 *
 */
@RestController
public class PartnerCACertEventController {
	
	private static final String CERTIFICATE_DATA = "certificateData";
	
	private static final String PARTNER_DOMAIN = "partnerDomain";
	
	private static Logger logger = IdaLogger.getLogger(PartnerCACertEventController.class);

	@Autowired
	private PartnerCertificateManagerService partnerCertManager;
	
	@PostMapping(value = "/callback/partnermanagement/" + PARTNER_CERT_EVENT, consumes = "application/json")
	@PreAuthenticateContentAndVerifyIntent(secret = "${" + IDA_WEBSUB_PARTNER_CERT_CALLBACK_SECRET
			+ "}", callback = "/idauthentication/v1/auth/callback/partnermanagement/" + PARTNER_CERT_EVENT, topic = "${" + IDA_WEBSUB_PARTNER_CERT_TOPIC + "}")
	public void handleCACertificate(@RequestBody EventModel eventModel) {
		logger.debug(IdAuthCommonConstants.SESSION_ID, "PartnerCACertEventController", "handleCACertificate", "EVENT RECEIVED");
		Map<String, Object> data = eventModel.getEvent().getData();
		CACertificateRequestDto caCertRequestDto = new CACertificateRequestDto();
		if (data.containsKey(CERTIFICATE_DATA) && data.get(CERTIFICATE_DATA) instanceof String) {
			caCertRequestDto.setCertificateData((String) data.get(CERTIFICATE_DATA));
		}
		if (data.containsKey(PARTNER_DOMAIN) && data.get(PARTNER_DOMAIN) instanceof String) {
			caCertRequestDto.setPartnerDomain((String) data.get(PARTNER_DOMAIN));
		}
		partnerCertManager.uploadCACertificate(caCertRequestDto);
	}

}
