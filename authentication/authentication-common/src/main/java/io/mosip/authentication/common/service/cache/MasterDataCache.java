package io.mosip.authentication.common.service.cache;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.RestHelper;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.dto.RestRequestDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * @author Manoj SP
 *
 */
@Component
public class MasterDataCache {

	/**
	 * IdTemplate Manager Logger
	 */
	private static Logger logger = IdaLogger.getLogger(MasterDataCache.class);

	/**
	 * The Rest request factory
	 */
	@Autowired
	private RestRequestFactory restFactory;

	@Autowired
	private Environment environment;

	/**
	 * The Rest Helper
	 */
	@Autowired
	@Qualifier("external")
	private RestHelper restHelper;

	@PostConstruct
	public void loadMasterData() throws IdAuthenticationBusinessException {
		getMasterDataTitles();
		getMasterDataTemplate(environment.getProperty(IdAuthConfigKeyConstants.AUTH_EMAIL_CONTENT_TEMPLATE));
		getMasterDataTemplate(environment.getProperty(IdAuthConfigKeyConstants.AUTH_EMAIL_SUBJECT_TEMPLATE));
		getMasterDataTemplate(environment.getProperty(IdAuthConfigKeyConstants.OTP_SUBJECT_TEMPLATE));
		getMasterDataTemplate(environment.getProperty(IdAuthConfigKeyConstants.OTP_CONTENT_TEMPLATE));
		getMasterDataTemplate(environment.getProperty(IdAuthConfigKeyConstants.AUTH_SMS_TEMPLATE));
		getMasterDataTemplate(environment.getProperty(IdAuthConfigKeyConstants.OTP_SMS_TEMPLATE));
	}

	@Cacheable("masterdata")
	public Map<String, Map<String, String>> getMasterDataTitles() throws IdAuthenticationBusinessException {
		try {
			return restHelper
					.requestSync(restFactory.buildRequest(RestServicesConstants.TITLE_SERVICE, null, Map.class));
		} catch (IDDataValidationException | RestServiceException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getName(), e.getErrorCode(),
					e.getErrorText());
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}

	@Cacheable("masterdata")
	public Map<String, Object> getMasterDataTemplate(String template) throws IdAuthenticationBusinessException {
		try {
			RestRequestDTO request = restFactory
					.buildRequest(RestServicesConstants.ID_MASTERDATA_TEMPLATE_SERVICE_MULTILANG, null, Map.class);
			request.setUri(request.getUri().replace("{code}", template));
			return restHelper.requestSync(request);
		} catch (IDDataValidationException | RestServiceException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getName(), e.getErrorCode(),
					e.getErrorText());
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}
}
