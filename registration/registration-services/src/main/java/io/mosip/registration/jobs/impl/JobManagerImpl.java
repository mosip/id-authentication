package io.mosip.registration.jobs.impl;

import java.util.HashMap;
import java.util.Map;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.entity.SyncJobDef;
import io.mosip.registration.jobs.JobManager;

@Component
public class JobManagerImpl implements JobManager {

	/**
	 * LOGGER for logging
	 */
	private static final Logger LOGGER = AppConfig.getLogger(JobManagerImpl.class);

	synchronized public String getJobId(JobExecutionContext context) {

		return getJobId(context.getJobDetail());
	}

	synchronized public String getJobId(JobDetail jobDetail) {

		return jobDetail.getKey().getName();
	}

	@Override
	synchronized public String getJobId(Trigger trigger) {
		return getJobId((JobDetail) trigger.getJobDataMap().get(RegistrationConstants.JOB_DETAIL));
	}

	@Override
	synchronized public Map<String, SyncJobDef> getChildJobs(final JobExecutionContext context) {

		LOGGER.info(LoggerConstants.BATCH_JOBS_SYNC_TRANSC_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Get Job started");

		// Get Job Map
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();

		Map<String, SyncJobDef> syncjobMap = new HashMap<>();

		jobDataMap.forEach((key, value) -> {

			// check whether the value is instance of sync job or not
			if (value instanceof SyncJobDef) {
				SyncJobDef syncJob = (SyncJobDef) value;
				syncjobMap.put(syncJob.getId(), syncJob);
			}

		});

		LOGGER.info(LoggerConstants.BATCH_JOBS_SYNC_TRANSC_LOGGER_TITLE, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "Get Job Ended");

		return syncjobMap;
	}

}
