package io.mosip.authentication.common.service.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
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
	
	@Autowired
	private Environment environment;

	/**
	 * Load master data.
	 *
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	// Invoking this in post construct does not work due to time-out issue happening
	// with webclient while invoking from post constuct.
	public void loadMasterData() throws IdAuthenticationBusinessException {
		masterDataCache.getMasterDataTitles();
		masterDataCache.getMasterDataTemplate(environment.getProperty(IdAuthConfigKeyConstants.AUTH_EMAIL_CONTENT_TEMPLATE));
		masterDataCache.getMasterDataTemplate(environment.getProperty(IdAuthConfigKeyConstants.AUTH_EMAIL_SUBJECT_TEMPLATE));
		masterDataCache.getMasterDataTemplate(environment.getProperty(IdAuthConfigKeyConstants.OTP_SUBJECT_TEMPLATE));
		masterDataCache.getMasterDataTemplate(environment.getProperty(IdAuthConfigKeyConstants.OTP_CONTENT_TEMPLATE));
		masterDataCache.getMasterDataTemplate(environment.getProperty(IdAuthConfigKeyConstants.AUTH_SMS_TEMPLATE));
		masterDataCache.getMasterDataTemplate(environment.getProperty(IdAuthConfigKeyConstants.OTP_SMS_TEMPLATE));
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
