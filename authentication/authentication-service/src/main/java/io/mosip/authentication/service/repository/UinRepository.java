package io.mosip.authentication.service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.authentication.service.entity.UinEntity;
import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;

/**
 * Repository to find the UinEntity
 * 
 * @author Arun Bose
 */
@Repository
public interface UinRepository extends BaseRepository<UinEntity, String> {

	/**
	 * 
	 * this method checks for uin
	 * 
	 * @return UinEntity
	 * @param uin
	 */
	Optional<UinEntity> findByUinRefId(String uinRefId);
	
	
	@Query("Select uinRefId from UinEntity where uinRefId = :refId")
	Optional<String> findUinByRefId(@Param("refId") String refId);
	
	@Query("Select uinEntity.id from UinEntity uinEntity INNER JOIN VIDEntity vidEntity ON uinEntity.uinRefId = vidEntity.refId where vidEntity.id = :vidNumber")
	Optional<String> findUinFromUinTableByJoinTableUinAndVid(@Param("vidNumber") String vidNumber);
}
