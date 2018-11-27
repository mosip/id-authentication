package io.mosip.registration.manager;

import java.util.Map;

import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;

import io.mosip.registration.entity.SyncJobDef;

public interface JobManager {

	/**
	 * To get the job by using jobContext
	 * 
	 * @param context
	 *            is a job information
	 * @return SyncJob the entity
	 */
	public Map<String, SyncJobDef> getChildJobs(JobExecutionContext context);

	/**
	 * To get JobId using Job Execution Context
	 * @param context job execution context
	 * @return job id
	 */
	public String getJobId(JobExecutionContext context);

	/**
	 * To get JobId using  JobDetail 
	 * @param jobDetail is job
	 * @return job id
	 */
	public String getJobId(JobDetail jobDetail);

	/**
	 * To get the job by using trigger information
	 * 
	 * @param trigger
	 *            class
	 * @return SyncJob the entity
	 */
	public String getJobId(Trigger trigger);

}
