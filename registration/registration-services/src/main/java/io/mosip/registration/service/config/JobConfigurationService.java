package io.mosip.registration.service.config;

import java.util.Map;

import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.entity.SyncJobDef;

/**
 * service for configuring jobs
 * 
 * @author YASWANTH S
 * @since 1.0.0
 *
 */
public interface JobConfigurationService {

	/**
	 * To get the list of {@link SyncJobDef}
	 * 
	 */
	void initiateJobs();

	/**
	 * Start the jobs with sheduled Time
	 * 
	 * @return responseDTO for start jobs
	 */
	ResponseDTO startScheduler();

	/**
	 * Stop the jobs manually
	 * 
	 * @return responseDTO for stop jobs
	 */
	ResponseDTO stopScheduler();

	/**
	 * To fetch the details of currently running job details
	 * 
	 * @return list of job names currently executing if success
	 */
	ResponseDTO getCurrentRunningJobDetails();

	/**
	 * execute the specified job
	 * 
	 * @param jobId
	 *            the job id
	 * @param triggerPoint where (User/System) the job was triggered
	 * @return responseDTO for execute job
	 */
	ResponseDTO executeJob(String jobId, String triggerPoint);

	/**
	 * Get Last Completed Sync Jobs from Sync Control
	 * 
	 * @return responseDTO for last completedJobs
	 */
	ResponseDTO getLastCompletedSyncJobs();

	/**
	 * Get history of Sync Jobs from Sync Transaction
	 * 
	 * @return responseDTO for last transaction of each syncJob
	 */
	ResponseDTO getSyncJobsTransaction();

	/**
	 * Run all the jobs
	 * 
	 * @return response of job
	 */
	ResponseDTO executeAllJobs();

	/**
	 * Is Application to be restart
	 * 
	 * @return response
	 */
	ResponseDTO isRestart();

	/**
	 * Get restart time
	 * 
	 * @return response
	 */
	ResponseDTO getRestartTime();
	
	/**
	 * Find whether scheduler running or not
	 * @return is scheduler running
	 */
	boolean isSchedulerRunning();
	
	/**
	 * Active Sync Job Map
	 * @return active sync map
	 */
	Map<String, SyncJobDef> getActiveSyncJobMap();
	

}
