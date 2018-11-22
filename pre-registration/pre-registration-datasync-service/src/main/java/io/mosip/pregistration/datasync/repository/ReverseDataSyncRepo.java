package io.mosip.pregistration.datasync.repository;


import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.pregistration.datasync.entity.PreRegistrationProcessedEntity;

/**
 * Repository for reverse data sync
 * 
 * @author M1046129
 *
 */
public interface ReverseDataSyncRepo extends BaseRepository<PreRegistrationProcessedEntity, String>{
	
}
