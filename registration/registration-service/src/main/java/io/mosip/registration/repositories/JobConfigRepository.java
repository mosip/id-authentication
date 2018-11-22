 package io.mosip.registration.repositories;

import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.SyncJobDef;

/**
 * To fetch the list of job details
 * @author Dinesh Ashokan
 *
 */
public interface JobConfigRepository extends BaseRepository<SyncJobDef, String>{

	/**
	 * To get list of active jobs
	 * 
	 * @return list of active sync jobs
	 */
	public List<SyncJobDef> findByIsActiveTrue();
}
