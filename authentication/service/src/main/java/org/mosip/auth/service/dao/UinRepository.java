package org.mosip.auth.service.dao;

import java.util.Optional;

import org.mosip.auth.service.entity.UinEntity;
import org.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import org.springframework.stereotype.Repository;

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
	public UinEntity findByUin(String uin);
	
	
	
	/**
	 * 
	 * this method checks for uin based on reference Id
	 * 
	 * @return UinEntity
	 * @param uin 
	 */
	public Optional<UinEntity> findById(String refId);
}
