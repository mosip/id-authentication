package io.mosip.registration.dao;

import java.util.List;

import io.mosip.registration.entity.SyncJob;

/**
 * DAO class for batch job configuration
 * 
 * @author Dinesh Ashokan
 *
 */
public interface JobConfigDAO {

	/**
	 * To get the List of {@link SyncJob}
	 * 
	 * @return list of sync jobs
	 */
	List<SyncJob> getAll();

	/**
	 * To get the List of {@link SyncJob}
	 * 
	 * @return list active sync jobs
	 */
	List<SyncJob> getActiveJobs();

}
