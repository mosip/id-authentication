package io.mosip.registration.repositories;

import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.SyncControl;

/**
 * Repository interface for Sync Job.
 *
 * @author Sreekar Chukka
 */
public interface SyncJobRepository extends BaseRepository<SyncControl, String>{

	/* (non-Javadoc)
	 * @see org.springframework.data.jpa.repository.JpaRepository#findAll()
	 */
	@Override
	List<SyncControl> findAll();
	
	SyncControl findBySyncJobId(String syncJobId);
}
