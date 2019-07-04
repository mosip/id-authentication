package io.mosip.registration.repositories;

import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.SyncJobDef;

public interface SyncJobDefRepository extends BaseRepository<SyncJobDef, String> {


	/**
	 * fetches all jobs that is active
	 *
	 * @return the list of SyncControl
	 */
	
	List<SyncJobDef> findAllByIsActiveTrue();
}
