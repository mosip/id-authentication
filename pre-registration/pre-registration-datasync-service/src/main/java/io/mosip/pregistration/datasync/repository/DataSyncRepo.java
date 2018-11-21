package io.mosip.pregistration.datasync.repository;

import java.sql.Timestamp;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.pregistration.datasync.entity.PreRegistrationEntity;

/**
 * @author M1043226
 *
 */
@Repository("DataSyncRepo")
@Transactional
public interface DataSyncRepo extends BaseRepository<PreRegistrationEntity, String>{
	
	
	public List<PreRegistrationEntity> findBycreateDateTimeBetween(Timestamp start,Timestamp end);
	
	
}
