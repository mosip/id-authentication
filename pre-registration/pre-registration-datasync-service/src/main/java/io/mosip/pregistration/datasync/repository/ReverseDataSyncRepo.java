package io.mosip.pregistration.datasync.repository;


import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.pregistration.datasync.entity.PreRegistrationProcessedEntity;

public interface ReverseDataSyncRepo extends BaseRepository<PreRegistrationProcessedEntity, String>{
	
}
