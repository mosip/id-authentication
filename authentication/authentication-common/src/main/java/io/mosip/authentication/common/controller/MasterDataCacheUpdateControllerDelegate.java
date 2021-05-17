package io.mosip.authentication.common.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.cache.MasterDataCache;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.websub.model.Event;
import io.mosip.kernel.core.websub.model.EventModel;

@Component
public class MasterDataCacheUpdateControllerDelegate {
	
	private static final String TEMPLATE_TYPE_CODE = "templateTypeCode";

	private static final String EMPTY_STRING = "";

	private static Logger logger = IdaLogger.getLogger(MasterDataCacheUpdateControllerDelegate.class);
	
	@Autowired
	private MasterDataCache masterDataCache;
	
	public void updateTemplates(EventModel model) {
		logger.debug(IdAuthCommonConstants.SESSION_ID, this.getClass().getCanonicalName(), "updateTemplates", "HANDLING EVENT");
		String template = getTemplateCode(model);
		masterDataCache.clearMasterDataTemplateCache(template);
		try {
			masterDataCache.getMasterDataTemplate(template);
		} catch (IdAuthenticationBusinessException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		}
		
	}
	
	private String getTemplateCode(EventModel model) {
		return Optional.ofNullable(model)
				.map(EventModel::getEvent)
				.map(Event::getData)
				.map(map -> map.get(TEMPLATE_TYPE_CODE))
				.map(String::valueOf)
				.orElse(EMPTY_STRING);
	}

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
