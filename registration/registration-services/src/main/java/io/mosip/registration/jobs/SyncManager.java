package io.mosip.registration.jobs;

import io.mosip.registration.entity.SyncControl;
import io.mosip.registration.entity.SyncTransaction;

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
	 * To create a new transaction in sync transaction table
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
			String syncJobId);

	/**
	 * @param syncTransaction
	 *            last transaction
	 * @return updated sync control for respective sync job transaction
	 */
	public SyncControl createSyncControlTransaction(SyncTransaction syncTransaction);

}
