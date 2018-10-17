package io.mosip.authentication.service.repository;

import org.springframework.stereotype.Repository;

import io.mosip.authentication.service.entity.UinEntity;
import io.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;

/**
 * Repository to find the UinEntity
 * @author Arun Bose
 */
@Repository
public interface UinRepository extends BaseRepository<UinEntity, String>{
	
	
	/**
	 * 
	 * this method checks for uin
	 * 
	 * @return UinEntity
	 * @param uin 
	 */
	public UinEntity findByUinRefId(String uinRefId);
}
