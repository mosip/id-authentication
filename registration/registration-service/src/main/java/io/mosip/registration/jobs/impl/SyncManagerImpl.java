package io.mosip.registration.jobs.impl;

import java.sql.Timestamp;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.MachineMappingDAO;
import io.mosip.registration.dao.SyncJobDAO;
import io.mosip.registration.dao.SyncJobTransactionDAO;
import io.mosip.registration.entity.SyncControl;
import io.mosip.registration.entity.SyncTransaction;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.jobs.SyncManager;
import io.mosip.registration.util.healthcheck.RegistrationSystemPropertiesChecker;

/**
 * This class includes the functionalities of what transaction table needed.,
 * like getting job details and preparation of sync transaction data
 * 
 * @author YASWANTH S
 * @since 1.0.0
 *
 */
@Component
public class SyncManagerImpl implements SyncManager {

	@Autowired
	private SyncJobTransactionDAO jobTransactionDAO;

	@Autowired
	private SyncJobDAO syncJobDAO;
	
	@Autowired
	private MachineMappingDAO machineMappingDAO;

	/**
	 * LOGGER for logging
	 */
	private static final Logger LOGGER = AppConfig.getLogger(SyncManagerImpl.class);

	

	@Override
	public SyncControl createSyncControlTransaction(final SyncTransaction syncTransaction) throws NullPointerException {

		SyncControl syncControl = syncJobDAO.findBySyncJobId(syncTransaction.getSyncJobId());

		boolean isNotCreated = syncControl == null;
		if (syncControl == null) {
			syncControl = new SyncControl();
			syncControl.setId(UUID.randomUUID().toString());
			syncControl.setSyncJobId(syncTransaction.getSyncJobId());
			syncControl.setIsActive(true);
			syncControl.setMachineId(RegistrationSystemPropertiesChecker.getMachineId());

			syncControl.setRegcntrId(syncTransaction.getCntrId());
			syncControl.setLangCode("EN");

			syncControl.setCrBy(SessionContext.getInstance().getUserContext().getUserId());
			syncControl.setCrDtime(new Timestamp(System.currentTimeMillis()));

		} else {
			syncControl.setUpdBy(SessionContext.getInstance().getUserContext().getUserId());
			syncControl.setUpdDtimes(new Timestamp(System.currentTimeMillis()));

		}
		syncControl.setSynctrnId(syncTransaction.getId());
		syncControl.setLastSyncDtimes(new Timestamp(System.currentTimeMillis()));

		if (isNotCreated) {
			syncControl = syncJobDAO.save(syncControl);
		} else {
			syncControl = syncJobDAO.update(syncControl);
		}
		return syncControl;

	}

	@Override
	public SyncTransaction createSyncTransaction(final String status, final String statusComment,
			final String triggerPoint, final String syncJobId) {
		LOGGER.debug(RegistrationConstants.BATCH_JOBS_SYNC_TRANSC_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Create Sync Transaction started");

		SyncTransaction syncTransaction = new SyncTransaction();

		try {

			
			syncTransaction.setId(UUID.randomUUID().toString());

			syncTransaction.setSyncJobId(syncJobId);

			syncTransaction.setSyncDateTime(new Timestamp(System.currentTimeMillis()));
			syncTransaction.setStatusCode(status);
			syncTransaction.setStatusComment(statusComment);

			// TODO
			syncTransaction.setTriggerPoint(triggerPoint);

			syncTransaction.setSyncFrom(RegistrationSystemPropertiesChecker.getMachineId());

			syncTransaction.setSyncTo(RegistrationConstants.JOB_SYNC_TO_SERVER);

			syncTransaction
					.setMachmId(machineMappingDAO.getStationID(RegistrationSystemPropertiesChecker.getMachineId()));

			if (SessionContext.getInstance().getUserContext().getRegistrationCenterDetailDTO() != null) {
				syncTransaction.setCntrId(SessionContext.getInstance().getUserContext().getRegistrationCenterDetailDTO()
						.getRegistrationCenterId());
			}

			syncTransaction.setLangCode("EN");

			
			syncTransaction.setCrBy(SessionContext.getInstance().getUserContext().getUserId());

			syncTransaction.setCrDtime(new Timestamp(System.currentTimeMillis()));

			syncTransaction = jobTransactionDAO.save(syncTransaction);

		} catch (RegBaseCheckedException exception) {
			LOGGER.error(RegistrationConstants.BATCH_JOBS_SYNC_TRANSC_LOGGER_TITLE,
					RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					exception.getMessage());
		} catch (NullPointerException nullPointerException) {
			LOGGER.error(RegistrationConstants.BATCH_JOBS_SYNC_TRANSC_LOGGER_TITLE,
					RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					nullPointerException.getMessage());

			throw new RegBaseUncheckedException(RegistrationConstants.SYNC_TRANSACTION_NULL_POINTER_EXCEPTION,
					nullPointerException.getMessage());
		}

		LOGGER.debug(RegistrationConstants.BATCH_JOBS_SYNC_TRANSC_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Create Sync Transaction Ended");

		return syncTransaction;
	}

}