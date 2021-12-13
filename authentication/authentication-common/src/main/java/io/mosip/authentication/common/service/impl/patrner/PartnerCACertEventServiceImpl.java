package io.mosip.authentication.common.service.impl.patrner;

import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.mosip.authentication.common.service.integration.DataShareManager;
import io.mosip.authentication.common.service.spi.websub.PartnerCACertEventService;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.idrepository.core.exception.RestServiceException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.websub.model.EventModel;
import io.mosip.kernel.partnercertservice.dto.CACertificateRequestDto;
import io.mosip.kernel.partnercertservice.service.spi.PartnerCertificateManagerService;

/**
 * The Class PartnerCACertEventServiceImpl.
 * 
 * @author Loganathan Sekar
 */
@Service
public class PartnerCACertEventServiceImpl implements PartnerCACertEventService {

	/** The Constant CERTIFICATE_DATA_SHARE_URL. */
	private static final String CERTIFICATE_DATA_SHARE_URL = "certChainDatashareUrl";
	
	/** The Constant PARTNER_DOMAIN. */
	private static final String PARTNER_DOMAIN = "partnerDomain";
	
	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(PartnerCACertEventServiceImpl.class);

	/** The data share manager. */
	@Autowired(required = false)
	private DataShareManager dataShareManager;
	
	/** The partner cert manager. */
	@Autowired
	private PartnerCertificateManagerService partnerCertManager;
	
	/** The decrypt ca cert from data share. */
	@Value("${ida-decrypt-ca-cert-data-share-content:false}")
	private boolean decryptCaCertFromDataShare;
	
	/**
	 * Handle CA cert event.
	 *
	 * @param eventModel the event model
	 * @throws RestServiceException the rest service exception
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	@Override
	public void handleCACertEvent(EventModel eventModel)
			throws RestServiceException, IdAuthenticationBusinessException {
		Map<String, Object> data = eventModel.getEvent().getData();
		CACertificateRequestDto caCertRequestDto = new CACertificateRequestDto();
		if (data.containsKey(CERTIFICATE_DATA_SHARE_URL) && data.get(CERTIFICATE_DATA_SHARE_URL) instanceof String) {
			String certificateDataShareUrl = (String) data.get(CERTIFICATE_DATA_SHARE_URL);
			caCertRequestDto.setCertificateData(downloadCertificate(certificateDataShareUrl));
		}
		if (data.containsKey(PARTNER_DOMAIN) && data.get(PARTNER_DOMAIN) instanceof String) {
			String partnerDomain = (String) data.get(PARTNER_DOMAIN);
			caCertRequestDto.setPartnerDomain(partnerDomain);
		}
		partnerCertManager.uploadCACertificate(caCertRequestDto);
	}

	/**
	 * Download certificate.
	 *
	 * @param certificateDataShareUrl the certificate data share url
	 * @return the string
	 * @throws RestServiceException the rest service exception
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	private String downloadCertificate(String certificateDataShareUrl)
			throws RestServiceException, IdAuthenticationBusinessException {
		try {
			return dataShareManager.downloadObject(certificateDataShareUrl, String.class,
					decryptCaCertFromDataShare);
		} catch (RestServiceException e) {
			logger.error("Error occured while downloading certificate from datashare : \n {}",
					e.getResponseBodyAsString());
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		} catch (IdAuthenticationBusinessException e) {
			logger.error("Error occured while downloading certificate from datashare : \n {}",
					ExceptionUtils.getStackTrace(e));
			throw e;
		}
	}

	@Override
	public void evictCACertCache(EventModel eventModel) throws RestServiceException, IdAuthenticationBusinessException {
		Map<String, Object> data = eventModel.getEvent().getData();
		if (data.containsKey(PARTNER_DOMAIN) && data.get(PARTNER_DOMAIN) instanceof String) {
			String partnerDomain = (String) data.get(PARTNER_DOMAIN);
			partnerCertManager.purgeTrustStoreCache(partnerDomain);
		}		
	}
}