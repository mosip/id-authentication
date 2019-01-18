package io.mosip.registration.dao.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.SyncTransactionDAO;
import io.mosip.registration.entity.SyncTransaction;
import io.mosip.registration.repositories.SyncTransactionRepository;

/**
 * implementation class of {@link SyncTransactionDAO}
 * 
 * @author Dinesh Ashokan
 *
 */
@Repository
public class SyncTransactionDAOImpl implements SyncTransactionDAO {

	/** Object for Logger. */
	private static final Logger LOGGER = AppConfig.getLogger(SyncTransactionDAOImpl.class);

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

		LOGGER.debug(RegistrationConstants.SYNC_TRANSACTION_DAO_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
				"saving sync details to databse started");
		return syncTranscRepository.save(syncTransaction);

	}
	
	/* (non-Javadoc)
	 * @see io.mosip.registration.dao.SyncJobTransactionDAO#getAll()
	 */
	@Override
	public List<SyncTransaction> getAll() {
		LOGGER.debug(RegistrationConstants.SYNC_TRANSACTION_DAO_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
				"Fetch all sync details from databse started");
		return syncTranscRepository.findAll();
	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.dao.SyncJobTransactionDAO#getSyncTransactions(java.sql.Timestamp)
	 */
	@Override
	public List<SyncTransaction> getSyncTransactions(Timestamp req) {
		LOGGER.debug(RegistrationConstants.SYNC_TRANSACTION_DAO_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
				"Fetch  sync details based on crDtime from databse started");
		return syncTranscRepository.findByCrDtimeAfter(req);
	}


}
