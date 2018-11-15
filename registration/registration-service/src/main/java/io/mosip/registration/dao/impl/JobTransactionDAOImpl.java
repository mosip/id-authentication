package io.mosip.registration.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.logger.logback.appender.RollingFileAppender;
import io.mosip.kernel.logger.logback.factory.Logfactory;
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
	private static Logger logger;

	@Autowired
	private SyncTransactionRepository syncTranscRepository;

	@Override
	public String saveSyncTransaction(SyncTransaction syncTransaction) {
		try {
			syncTranscRepository.save(syncTransaction);
			return "SAVED";
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.SYNC_JOB_RUN_TIME_EXCEPTION,
					runtimeException.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.dao.JobProcessorDAO#upadteSyncControl(io.mosip.
	 * registration.entity.SyncControl)
	 */
	@Override
	public String upadteSyncControl(SyncControl syncControl) {
		// TODO Auto-generated method stub
		return null;
	}

}
