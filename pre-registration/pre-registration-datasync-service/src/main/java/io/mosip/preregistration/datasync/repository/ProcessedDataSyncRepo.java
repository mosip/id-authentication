package io.mosip.preregistration.datasync.repository;


import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.preregistration.datasync.entity.ProcessedPreRegEntity;

/**
 * Repository for reverse data sync
 * 
 * @author M1046129
 *
 */
public interface ProcessedDataSyncRepo extends BaseRepository<ProcessedPreRegEntity, String>{

	
}
