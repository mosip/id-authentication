package io.mosip.registration.jobs.impl;

import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.esotericsoftware.kryo.Registration;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.audit.AuditManagerService;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.entity.RegistrationTransaction;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.jobs.BaseJob;

/**
 * The {@code DeleteAuditLogsJob} is a job to delete audit logs which extends
 * {@code BaseJob}
 * 
 * <p>
 * The Audit logs deletion was dependent on audit_deletion_configured_days.
 * configuration parameter, and Audit logs deletion was internally deletes the
 * registration packets in local and {@link Registration} and
 * {@link RegistrationTransaction} in database.
 * </p>
 * 
 * <p>
 * If audit_deletion_configured_days=10, then all audit logs before 10 days from
 * now where eligible to delete, and it has a internal check as the registration
 * associated with audit has to be processed, then delete.
 * </p>
 * 
 * <p>
 * This Job will be automatically triggered based on sync_frequency which has in
 * local DB.
 * </p>
 * 
 * <p>
 * If Sync_frequency = "0 0 11 * * ?" this job will be triggered everyday 11:00
 * AM, if it was missed on 11:00 AM, trigger on immediate application launch.
 * </p>
 * 
 * 
 * @author Yaswanth S
 * @since 1.0.0
 *
 */
@Component(value = "deleteAuditLogsJob")
public class DeleteAuditLogsJob extends BaseJob {

	/**
	 * The RegPacketStatusServiceImpl
	 */
	@Autowired
	private AuditManagerService auditService;

	/**
	 * LOGGER for logging
	 */
	private static final Logger LOGGER = AppConfig.getLogger(DeleteAuditLogsJob.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.scheduling.quartz.QuartzJobBean#executeInternal(org.
	 * quartz.JobExecutionContext)
	 */
	@Async
	@Override
	public void executeInternal(JobExecutionContext context) {
		LOGGER.debug(LoggerConstants.DELETE_AUDIT_LOGS_JOB, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "job execute internal started");
		this.responseDTO = new ResponseDTO();

		try {

			this.jobId = loadContext(context);
			auditService = applicationContext.getBean(AuditManagerService.class);

			/* Run the Parent JOB always first */
			this.responseDTO = auditService.deleteAuditLogs();

			/* To run the child jobs after the parent job Success */
			if (responseDTO.getSuccessResponseDTO() != null) {
				executeChildJob(jobId, jobMap);
			}

			syncTransactionUpdate(responseDTO, triggerPoint, jobId);

		} catch (RegBaseUncheckedException baseUncheckedException) {
			LOGGER.error(LoggerConstants.DELETE_AUDIT_LOGS_JOB, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					baseUncheckedException.getMessage() + ExceptionUtils.getStackTrace(baseUncheckedException));
			throw baseUncheckedException;
		}

		LOGGER.debug(LoggerConstants.DELETE_AUDIT_LOGS_JOB, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "job execute internal Ended");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.jobs.BaseJob#executeJob(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public ResponseDTO executeJob(String triggerPoint, String jobId) {

		LOGGER.debug(LoggerConstants.DELETE_AUDIT_LOGS_JOB, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "execute Job started");
		this.responseDTO = auditService.deleteAuditLogs();
		syncTransactionUpdate(responseDTO, triggerPoint, jobId);

		LOGGER.debug(LoggerConstants.DELETE_AUDIT_LOGS_JOB, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "execute job ended");

		return responseDTO;

	}

}
