package io.mosip.registration.manager.impl;

import java.sql.Timestamp;
import java.util.Random;

import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.JobTransactionDAO;
import io.mosip.registration.entity.SyncJob;
import io.mosip.registration.entity.SyncTransaction;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.manager.BaseTransactionManager;
import io.mosip.registration.service.impl.JobConfigurationServiceImpl;
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
public class SyncTransactionManagerImpl implements BaseTransactionManager {

	@Autowired
	JobTransactionDAO jobTransactionDAO;

	/**
	 * LOGGER for logging
	 */
	private static final MosipLogger LOGGER = AppConfig.getLogger(SyncTransactionManagerImpl.class);

	// Need to be removed if the transaction table's primary key ID is Auto -
	// Generatable
	Random random = new Random();

	@Override
	public SyncJob getJob(JobExecutionContext context) {
		LOGGER.debug(RegistrationConstants.BATCH_JOBS_SYNC_TRANSC_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Get Job started");

		SyncJob syncJob = null;
		try {
			syncJob = getJob(context.getJobDetail());

		} catch (NullPointerException nullPointerException) {
			throw new RegBaseUncheckedException(RegistrationConstants.SYNC_TRANSACTION_NULL_POINTER_EXCEPTION,
					nullPointerException.getMessage());
		}
		LOGGER.debug(RegistrationConstants.BATCH_JOBS_SYNC_TRANSC_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Get Job Ended");

		return syncJob;
	}

	@Override
	public SyncJob getJob(String apiName) {
		LOGGER.debug(RegistrationConstants.BATCH_JOBS_SYNC_TRANSC_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Get Job started");

		SyncJob syncJob = null;
		try {
			syncJob = JobConfigurationServiceImpl.SYNC_JOB_MAP.get(apiName.toLowerCase());

		} catch (NullPointerException nullPointerException) {
			throw new RegBaseUncheckedException(RegistrationConstants.SYNC_TRANSACTION_NULL_POINTER_EXCEPTION,
					nullPointerException.getMessage());
		}
		LOGGER.debug(RegistrationConstants.BATCH_JOBS_SYNC_TRANSC_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Get Job Ended");

		return syncJob;
	}

	@Override
	public SyncJob getJob(Trigger trigger) {
		LOGGER.debug(RegistrationConstants.BATCH_JOBS_SYNC_TRANSC_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Get Job started");

		SyncJob syncJob = null;
		try {
			syncJob = getJob((JobDetail) trigger.getJobDataMap().get("jobDetail"));

		} catch (NullPointerException nullPointerException) {
			throw new RegBaseUncheckedException(RegistrationConstants.SYNC_TRANSACTION_NULL_POINTER_EXCEPTION,
					nullPointerException.getMessage());
		}
		LOGGER.debug(RegistrationConstants.BATCH_JOBS_SYNC_TRANSC_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Get Job Ended");

		return syncJob;
	}

	@Override
	public SyncJob getJob(JobDetail jobDetail) {

		LOGGER.debug(RegistrationConstants.BATCH_JOBS_SYNC_TRANSC_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Get Job started");

		SyncJob syncJob = null;
		try {

			String jobId = jobDetail.getKey().getName();
			syncJob = JobConfigurationServiceImpl.SYNC_JOB_MAP.get(jobId);

		} catch (NullPointerException nullPointerException) {
			throw new RegBaseUncheckedException(RegistrationConstants.SYNC_TRANSACTION_NULL_POINTER_EXCEPTION,
					nullPointerException.getMessage());
		}
		LOGGER.debug(RegistrationConstants.BATCH_JOBS_SYNC_TRANSC_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Get Job Ended");

		return syncJob;
	}

	@Override
	public void createSyncTransaction(String status, String statusComment, String triggerPoint, SyncJob syncJob)
			throws RegBaseUncheckedException {
		LOGGER.debug(RegistrationConstants.BATCH_JOBS_SYNC_TRANSC_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Create Sync Transaction started");

		SyncTransaction syncTransaction = new SyncTransaction();
		try {
			// TODO to be auto generated and has to be remove from here
			syncTransaction.setId(Integer.toString(random.nextInt(100)));

			syncTransaction.setSyncJobId(syncJob.getId());
			syncTransaction.setSyncDateTime(new Timestamp(System.currentTimeMillis()));
			syncTransaction.setStatusCode(status);
			syncTransaction.setStatusComment(statusComment);

			// TODO
			syncTransaction.setTriggerPoint(triggerPoint);

			syncTransaction.setSyncFrom(RegistrationSystemPropertiesChecker.getMachineId());

			// TODO
			syncTransaction.setSyncTo("SERVER???");

			syncTransaction.setMachmId(RegistrationSystemPropertiesChecker.getMachineId());
			// syncTransaction.setCntrId(SessionContext.getInstance().getUserContext().getRegistrationCenterDetailDTO()
			// .getRegistrationCenterId());

			// TODO
			/*
			 * syncTransaction.setRefId("REFID"); syncTransaction.setRefType("REFTYPE");
			 * syncTransaction.setSyncParam("SyncParam");
			 */

			// TODO
			syncTransaction.setLangCode("EN");

			syncTransaction.setActive(true);

			syncTransaction.setCrBy(SessionContext.getInstance().getUserContext().getUserId());
			syncTransaction.setCrDtime(new Timestamp(System.currentTimeMillis()));

			// TODO
			// update by and timez info

			// TODO
			// ISDeleted and Timez info

			//

			jobTransactionDAO.saveSyncTransaction(syncTransaction);
		} catch (NullPointerException nullPointerException) {
			throw new RegBaseUncheckedException(RegistrationConstants.SYNC_TRANSACTION_NULL_POINTER_EXCEPTION,
					nullPointerException.getMessage());
		}

		LOGGER.debug(RegistrationConstants.BATCH_JOBS_SYNC_TRANSC_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Create Sync Transaction Ended");

	}

}
