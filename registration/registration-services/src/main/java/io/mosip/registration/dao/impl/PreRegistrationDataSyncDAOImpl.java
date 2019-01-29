package io.mosip.registration.dao.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.PreRegistrationDataSyncDAO;
import io.mosip.registration.entity.GlobalParam;
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
	public PreRegistrationList get(String preRegId) {
		
		LOGGER.debug("REGISTRATION - PRE_REGISTRATION_DATA_SYNC - PRE_REGISTRATION_DATA_SYNC_DAO_IMPL", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Fetching Pre-Registration");


		return preRegistrationRepository.findByPreRegId(preRegId);
		
	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.dao.PreRegistrationDAO#savePreRegistration(io.mosip.registration.entity.PreRegistration)
	 */
	@Override
	public PreRegistrationList save(PreRegistrationList preRegistration) {
		
		LOGGER.debug("REGISTRATION - PRE_REGISTRATION_DATA_SYNC - PRE_REGISTRATION_DATA_SYNC_DAO_IMPL", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Saving Pre-Registration");

		return preRegistrationRepository.save(preRegistration);
		
	}
	
	public List<PreRegistrationList> fetchRecordsToBeDeleted(Date startDate){
		
		LOGGER.debug("REGISTRATION - PRE_REGISTRATION_DATA_SYNC_RECORD_FETCH - PRE_REGISTRATION_DATA_SYNC_DAO_IMPL", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Fetch Records that needs to be deleted");
		
		return preRegistrationRepository.findByAppointmentDateBeforeAndIsDeleted(startDate, false);
	}
	
	public PreRegistrationList update(PreRegistrationList preReg){
		
		LOGGER.debug("REGISTRATION - PRE_REGISTRATION_DATA_SYNC_RECORD_UPDATE - PRE_REGISTRATION_DATA_SYNC_DAO_IMPL", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Update the deleted records");
		
		return preRegistrationRepository.update(preReg);
		
	}

	@Override
	public void deleteAll(List<PreRegistrationList> preRegistrationLists) {
		LOGGER.debug("REGISTRATION - PRE_REGISTRATION_DATA_SYNC_RECORD_UPDATE - PRE_REGISTRATION_DATA_SYNC_DAO_IMPL", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Delete records started");
		/** Parase List to Iterable */
		Iterable<PreRegistrationList> iterablePreRegistrationList = preRegistrationLists;

		preRegistrationRepository.deleteInBatch(iterablePreRegistrationList);
		LOGGER.debug("REGISTRATION - PRE_REGISTRATION_DATA_SYNC_RECORD_UPDATE - PRE_REGISTRATION_DATA_SYNC_DAO_IMPL", RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "delete records ended");
		
	}

}
