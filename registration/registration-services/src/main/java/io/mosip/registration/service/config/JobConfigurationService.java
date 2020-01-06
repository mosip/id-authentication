package io.mosip.registration.service.config;

import java.util.List;
import java.util.Map;

import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.entity.SyncControl;
import io.mosip.registration.entity.SyncJobDef;

/**
 * This interface manages all the jobs with respect to registration client application. 
 * It pulls the list of active jobs and respective execution configuration from db table. 
 * It creates the Scheduler object by setting the required jobs and initiate the process. 
 * It provides the required functions to trigger the specific jobs at required time. And also providing additional method to manage the jobs.  
 * It associates with JobTrigger and JobProcessListener to update the each state of a job into the table. 
 * 
 * 
 * @author YASWANTH S
 * @since 1.0.0
 */
public interface JobConfigurationService {

	/**
	 * Once this object has been created then it gets the list of jobs from database and prepare the scheduler object. 
	 * If any jobs are missed to execute as per the configuration then those jobs would be triggered. 
	 */
	void initiateJobs();

	/**
	 * Once the application started, this method would be invoked to initiate the Scheduler to Start the jobs at the configured frequency. 
	 * 
	 * @return responseDTO 
	 * 			Contains success or failure response. 
	 */
	ResponseDTO startScheduler();

	/**
	 * It helps to stop the scheduler at required time.
	 * 
	 * @return responseDTO for stop jobs
	 */
	ResponseDTO stopScheduler();

	/**
	 * To fetch the details of currently running job
	 * 
	 * @return list of job names currently executing if success
	 */
	ResponseDTO getCurrentRunningJobDetails();

	/**
	 * It helps to execute the specified job at required time from UI application. 
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
	 * Get history of Sync Jobs from Sync Transaction table.
	 * 
	 * @return responseDTO for last transaction of each syncJob
	 */
	ResponseDTO getSyncJobsTransaction();

	/**
	 * It pulls all the jobs from table and forcefully execute it. 
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
	 * @return 
	 * 		true, if application running.
	 * 		false, if application not running. 
	 */
	boolean isSchedulerRunning();
	
	/**
	 * It returns the active Sync Job, which was fetched from DB.
	 * 
	 * @return 
	 * 		Map contains the list of sync job names and the respective Job Object.
	 */
	Map<String, SyncJobDef> getActiveSyncJobMap();
	
	/**
	 * It returns the offline Sync Job, which was fetched from DB.
	 * 
	 * @return 
	 * 		List contains the list of sync job names .
	 */
	public List<String> getOfflineJobs();
	
	/**
	 * It returns the Untagged Job, which was fetched from DB.
	 * 
	 * @return 
	 * 		List contains the list of sync job names .
	 */
	public List<String> getUnTaggedJobs();
	
	/**
	 * 
	 * It returns syncControl of the input parameters job if exists, otherwise returns null
	 * @param syncJobId sync job id
	 * @return SyncControl entity
	 */
	public SyncControl getSyncControlOfJob(String syncJobId);

	

}
