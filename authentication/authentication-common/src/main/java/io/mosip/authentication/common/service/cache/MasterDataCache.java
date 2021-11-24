package io.mosip.authentication.common.service.cache;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.idrepository.core.dto.RestRequestDTO;
import io.mosip.idrepository.core.exception.RestServiceException;
import io.mosip.idrepository.core.helper.RestHelper;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * The Class MasterDataCache.
 *
 * @author Manoj SP
 */
@Component
public class MasterDataCache {

	/** The Constant MASTERDATA_TITLES. */
	private static final String MASTERDATA_TITLES = "masterdata/titles";

	/** The Constant MASTERDATA_TEMPLATES. */
	private static final String MASTERDATA_TEMPLATES = "masterdata/templates";

	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(MasterDataCache.class);

	/** The Rest request factory. */
	@Autowired
	private RestRequestFactory restFactory;

	/** The Rest Helper. */
	@Autowired
	@Qualifier("withSelfTokenWebclient")
	private RestHelper restHelper;

	/**
	 * Gets the master data titles.
	 *
	 * @return the master data titles
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	@Cacheable(cacheNames = MASTERDATA_TITLES)
	public Map<String, Object> getMasterDataTitles() throws IdAuthenticationBusinessException {
		try {
			return restHelper
					.requestSync(restFactory.buildRequest(RestServicesConstants.TITLE_SERVICE, null, Map.class));
		} catch (IDDataValidationException | RestServiceException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getName(), e.getErrorCode(),
					e.getErrorText());
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}
	
	/**
	 * Gets the master data template.
	 *
	 * @param template the template
	 * @return the master data template
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	@Cacheable(cacheNames = MASTERDATA_TEMPLATES, key = "#template")
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
	
	/**
	 * Clear master data template cache.
	 *
	 * @param template the template
	 */
	@CacheEvict(value=MASTERDATA_TEMPLATES, key = "#template")
	public void clearMasterDataTemplateCache(String template) {
		logger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "clearMasterDataTemplateCache",
				"masterdata cache cleared for template code: " + template);
	}
	
	/**
	 * Clear master data titles cache.
	 */
	@CacheEvict(value=MASTERDATA_TITLES)
	public void clearMasterDataTitlesCache() {
		logger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "clearMasterDataTitlesCache",
				"masterdata cache cleared for titles");
	}

}
