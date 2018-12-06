package io.mosip.registration.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.PreRegistrationDataSyncDAO;
import io.mosip.registration.entity.PreRegistrationList;
import io.mosip.registration.repositories.PreRegistrationDataSyncRepository;


/**
 * {@link PreRegistrationDataSyncDAO}
 * @author YASWANTH S
 * @since 1.0.0
 *
 */
@Component
public class PreRegistrationDataSyncDAOImpl implements PreRegistrationDataSyncDAO {
	
	/**
	 * Autowires Pre Registration Repository class
	 */
	@Autowired
	PreRegistrationDataSyncRepository preRegistrationRepository;
	
	/**
	 * Instance of LOGGER
	 */
	private static final Logger LOGGER = AppConfig.getLogger(PreRegistrationDataSyncDAOImpl.class);


	/* (non-Javadoc)
	 * @see io.mosip.registration.dao.PreRegistrationDAO#getPreRegistration(java.lang.String)
	 */
	@Override
	public PreRegistrationList getPreRegistration(String preRegId) {
		
		LOGGER.debug("REGISTRATION - PRE_REGISTRATION_DATA_SYNC - PRE_REGISTRATION_DATA_SYNC_DAO_IMPL", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Fetching Pre-Registration");


		return preRegistrationRepository.findByPreRegId(preRegId);
		
	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.dao.PreRegistrationDAO#savePreRegistration(io.mosip.registration.entity.PreRegistration)
	 */
	@Override
	public PreRegistrationList savePreRegistration(PreRegistrationList preRegistration) {
		
		LOGGER.debug("REGISTRATION - PRE_REGISTRATION_DATA_SYNC - PRE_REGISTRATION_DATA_SYNC_DAO_IMPL", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Saving Pre-Registration");

		return preRegistrationRepository.save(preRegistration);
		
	}

}
