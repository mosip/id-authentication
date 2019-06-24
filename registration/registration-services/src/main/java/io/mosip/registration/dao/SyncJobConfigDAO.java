package io.mosip.registration.dao;

import java.util.List;

import io.mosip.registration.entity.SyncJobDef;

/**
 * DAO class for all the Sync Job related details
 * 
 * @author Dinesh Ashokan
 *
 */
public interface SyncJobConfigDAO {

	/**
	 * To get all jobs in the List of {@link SyncJobDef}
	 * 
	 * @return list of sync jobs
	 */
	List<SyncJobDef> getAll();

	/**
	 * To get all the List of active {@link SyncJobDef}
	 * 
	 * @return list active sync jobs
	 */
	List<SyncJobDef> getActiveJobs();

	/**
	 * Update all the Syncjobs available in the {@link SyncJobDef} list
	 * 
	 * @param syncJobDefs
	 *            list
	 * @return updated syncJobs
	 */
	List<SyncJobDef> updateAll(List<SyncJobDef> syncJobDefs);

}
