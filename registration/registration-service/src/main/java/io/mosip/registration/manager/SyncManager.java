package io.mosip.registration.manager;

import java.util.List;
import java.util.Map;

import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;

import io.mosip.registration.entity.SyncControl;
import io.mosip.registration.entity.SyncJobDef;
import io.mosip.registration.entity.SyncTransaction;
import io.mosip.registration.exception.RegBaseUncheckedException;

/**
 * This class includes the functionalities of what transaction table needed.,
 * like getting job details and preparation of sync transaction data
 * 
 * @author YASWANTH S
 * @since 1.0.0
 *
 */
public interface SyncManager {

	
	
	/**
	 * To create a new transacion in sync transaction table
	 * 
	 * @param status
	 *            of Job
	 * @param statusComment
	 *            of job
	 * @param triggerPoint
	 *            information system/User
	 * 
	 * @param syncJobId
	 *            information of job
	 * @return last inserted sync transaction
	 */
	public SyncTransaction createSyncTransaction(String status, String statusComment, String triggerPoint,
			String syncJobId) throws RegBaseUncheckedException;
	
	/**
	 * @param syncTransaction
	 *            last transaction
	 * @return updated sync control for respective sync job transaction
	 */
	public SyncControl createSyncControlTransaction(SyncTransaction syncTransaction);


	/**
	 * @param syncTransaction
	 *            last transaction
	 * @return updated sync control for respective sync job transaction
	 *//*
	public SyncControl createSyncControlTransaction(SyncTransaction syncTransaction);
	
	
	public String getJobId(JobExecutionContext context);
	
	public String getJobId(JobDetail jobDetail);
	
	*//**
	 * To get the job by using trigger information
	 * 
	 * @param trigger
	 *            class
	 * @return SyncJob the entity
	 *//*
	public String getJobId(Trigger trigger);
	
	*//**
	 * To get the job by using jobContext
	 * 
	 * @param context
	 *            is a job information
	 * @return SyncJob the entity
	 *//*
	public Map<String, SyncJobDef> getChildJobs(JobExecutionContext context);
*/
}
