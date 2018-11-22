package io.mosip.registration.dao;

import java.util.List;

import io.mosip.registration.entity.SyncJobDef;

/**
 * DAO class for batch job configuration
 * 
 * @author Dinesh Ashokan
 *
 */
public interface SyncJobConfigDAO {

	/**
	 * To get the List of {@link SyncJobDef}
	 * 
	 * @return list of sync jobs
	 */
	List<SyncJobDef> getAll();

	/**
	 * To get the List of {@link SyncJobDef}
	 * 
	 * @return list active sync jobs
	 */
	List<SyncJobDef> getActiveJobs();

}
