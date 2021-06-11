package io.mosip.authentication.internal.service.controller;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.CA_CERT_EVENT;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_CA_CERT_CALLBACK_SECRET;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_CA_CERT_TOPIC;

import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.authentication.common.service.integration.DataShareManager;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.websub.model.EventModel;
import io.mosip.kernel.partnercertservice.dto.CACertificateRequestDto;
import io.mosip.kernel.partnercertservice.service.spi.PartnerCertificateManagerService;
import io.mosip.kernel.websub.api.annotation.PreAuthenticateContentAndVerifyIntent;

/**
 * @author Manoj SP
 *
 */
@RestController
public class PartnerCACertEventController {
	
	private static final String CERTIFICATE_DATA_SHARE_URL = "certChainDatashareUrl";
	
	private static final String PARTNER_DOMAIN = "partnerDomain";
	
	private static Logger logger = IdaLogger.getLogger(PartnerCACertEventController.class);
	
	@Autowired
	private DataShareManager dataShareManager;

	@Autowired
	private PartnerCertificateManagerService partnerCertManager;

	@Value("${ida-decrypt-ca-cert-data-share-content:false}")
	private boolean decryptCaCertFromDataShare;
	
	@PostMapping(value = "/callback/partnermanagement/" + CA_CERT_EVENT, consumes = "application/json")
	@PreAuthenticateContentAndVerifyIntent(secret = "${" + IDA_WEBSUB_CA_CERT_CALLBACK_SECRET
			+ "}", callback = "/idauthentication/v1/internal/callback/partnermanagement/" + CA_CERT_EVENT, topic = "${" + IDA_WEBSUB_CA_CERT_TOPIC + "}")
	public void handleCACertificate(@RequestBody EventModel eventModel) throws RestServiceException, IdAuthenticationBusinessException {
		logger.debug(IdAuthCommonConstants.SESSION_ID, "PartnerCACertEventController", "handleCACertificate", "EVENT RECEIVED");
		handleCACertEvent(eventModel);
	}

	public void handleCACertEvent(EventModel eventModel) throws RestServiceException, IdAuthenticationBusinessException {
		Map<String, Object> data = eventModel.getEvent().getData();
		CACertificateRequestDto caCertRequestDto = new CACertificateRequestDto();
		if (data.containsKey(CERTIFICATE_DATA_SHARE_URL) && data.get(CERTIFICATE_DATA_SHARE_URL) instanceof String) {
			String certificateDataShareUrl = (String) data.get(CERTIFICATE_DATA_SHARE_URL);
			caCertRequestDto.setCertificateData(downloadCertificate(certificateDataShareUrl));
		}
		if (data.containsKey(PARTNER_DOMAIN) && data.get(PARTNER_DOMAIN) instanceof String) {
			caCertRequestDto.setPartnerDomain((String) data.get(PARTNER_DOMAIN));
		}
		partnerCertManager.uploadCACertificate(caCertRequestDto);
	}

	private String downloadCertificate(String certificateDataShareUrl) throws RestServiceException, IdAuthenticationBusinessException {
		try {
			return dataShareManager.downloadObject(certificateDataShareUrl, String.class, decryptCaCertFromDataShare);
		} catch (RestServiceException e) {
			logger.error("Error occured while downloading certificate from datashare : \n {}",e.getResponseBodyAsString());
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		} catch (IdAuthenticationBusinessException e) {
			logger.error("Error occured while downloading certificate from datashare : \n {}", ExceptionUtils.getStackTrace(e));
			throw e;
		}
	}

}
