package io.mosip.authentication.common.service.impl.masterdata;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.authentication.common.service.cache.MasterDataCache;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.masterdata.MasterDataCacheUpdateService;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.websub.model.Event;
import io.mosip.kernel.core.websub.model.EventModel;

/**
 * The Class MasterDataCacheUpdateServiceImpl.
 * 
 * @author Loganathan Sekar
 */
@Service
public class MasterDataCacheUpdateServiceImpl implements MasterDataCacheUpdateService {
	
	/** The Constant TEMPLATES. */
	private static final String TEMPLATES = "templates";

	/** The Constant TEMPLATE_TYPE_CODE. */
	private static final String TEMPLATE_TYPE_CODE = "templateTypeCode";

	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(MasterDataCacheUpdateServiceImpl.class);
	
	/** The master data cache. */
	@Autowired
	private MasterDataCache masterDataCache;
	
	/**
	 * Update templates.
	 *
	 * @param model the model
	 */
	@Override
	public void updateTemplates(EventModel model) {
		logger.debug(IdAuthCommonConstants.SESSION_ID, this.getClass().getCanonicalName(), "updateTemplates", "HANDLING EVENT");
		getTemplateCode(model).ifPresent(template -> {
			masterDataCache.clearMasterDataTemplateCache(template);
			try {
				masterDataCache.getMasterDataTemplate(template);
			} catch (IdAuthenticationBusinessException e) {
				logger.error(ExceptionUtils.getStackTrace(e));
			}
		});
	}
	
	/**
	 * Gets the template code.
	 *
	 * @param model the model
	 * @return the template code
	 */
	private Optional<String> getTemplateCode(EventModel model) {
		return Optional.ofNullable(model)
				.map(EventModel::getEvent)
				.map(Event::getData)
				.map(map -> map.get(TEMPLATES))
				.filter(obj -> obj instanceof Map)
				.map(obj -> ((Map<String, Object>)obj).get(TEMPLATE_TYPE_CODE))
				.map(String::valueOf);
	}

	/**
	 * Update titles.
	 *
	 * @param model the model
	 */
	@Override
	public void updateTitles(EventModel model) {
		logger.debug(IdAuthCommonConstants.SESSION_ID, this.getClass().getCanonicalName(), "updateTitles", "HANDLING EVENT");
		masterDataCache.clearMasterDataTitlesCache();
		try {
			masterDataCache.getMasterDataTitles();
		} catch (IdAuthenticationBusinessException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		}
	}
	

}
