package io.mosip.registration.dao.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.SyncJobTransactionDAO;
import io.mosip.registration.entity.SyncControl;
import io.mosip.registration.entity.SyncTransaction;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.repositories.SyncTransactionRepository;

/**
 * implementation class of {@link SyncJobTransactionDAO}
 * 
 * @author Dinesh Ashokan
 *
 */
@Repository
public class JobTransactionDAOImpl implements SyncJobTransactionDAO {

	/** Object for Logger. */
	private static final Logger LOGGER = AppConfig.getLogger(JobTransactionDAOImpl.class);

	/**
	 * Autowired to sync transaction Repository
	 */
	@Autowired
	private SyncTransactionRepository syncTranscRepository;

	/* (non-Javadoc)
	 * @see io.mosip.registration.dao.JobTransactionDAO#save(io.mosip.registration.entity.SyncTransaction)
	 */
	@Override
	public SyncTransaction save(SyncTransaction syncTransaction) {

		LOGGER.debug("REGISTRATION - SYNC - VALIDATION", APPLICATION_NAME, APPLICATION_ID,
				"saving sync details to databse started");
		return syncTranscRepository.save(syncTransaction);

	}


}
