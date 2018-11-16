package io.mosip.registration.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.JobTransactionDAO;
import io.mosip.registration.entity.SyncControl;
import io.mosip.registration.entity.SyncTransaction;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.repositories.SyncTransactionRepository;

/**
 * implementation class of {@link JobTransactionDAO}
 * 
 * @author Dinesh Ashokan
 *
 */
@Repository
public class JobTransactionDAOImpl implements JobTransactionDAO {

	/** Object for Logger. */
	private static final Logger LOGGER = AppConfig.getLogger(JobTransactionDAOImpl.class);

	@Autowired
	private SyncTransactionRepository syncTranscRepository;

	@Override
	public SyncTransaction saveSyncTransaction(SyncTransaction syncTransaction) {

		return syncTranscRepository.save(syncTransaction);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.dao.JobProcessorDAO#upadteSyncControl(io.mosip.
	 * registration.entity.SyncControl)
	 */
	@Override
	public String upadteSyncControl(SyncControl syncControl) {
		return null;
	}

}
