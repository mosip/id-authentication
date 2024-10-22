package io.mosip.authentication.common.service.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthUncheckedException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

/**
 * The Class MasterDataCacheInitializer.
 *
 * @author Loganathan Sekar
 */
@Component
public class MasterDataCacheInitializer implements ApplicationListener<ApplicationReadyEvent>{

	@Autowired
	private MasterDataCache masterDataCache;

	/**
	 * Load master data.
	 *
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	// Invoking this in post construct does not work due to time-out issue happening
	// with webclient while invoking from post constuct.
	public void loadMasterData() throws IdAuthenticationBusinessException {
		masterDataCache.getMasterDataTitles();
		masterDataCache.getMasterDataTemplate(EnvUtil.getAuthEmailContentTemplate());
		masterDataCache.getMasterDataTemplate(EnvUtil.getAuthEmailSubjectTemplate());
		masterDataCache.getMasterDataTemplate(EnvUtil.getOtpSubjectTemplate());
		masterDataCache.getMasterDataTemplate(EnvUtil.getOtpContentTemplate());
		masterDataCache.getMasterDataTemplate(EnvUtil.getAuthSmsTemplate());
		masterDataCache.getMasterDataTemplate(EnvUtil.getOtpSmsTemplate());
	}
	

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		try {
			loadMasterData();
		} catch (IdAuthenticationBusinessException e) {
			throw new IdAuthUncheckedException(IdAuthenticationErrorConstants.SERVER_ERROR, e);
		}		
	}
}
