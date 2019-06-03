package io.mosip.idrepository.identity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.mosip.idrepository.identity.entity.UinHistory;

/**
 * The Interface UinHistoryRepo.
 *
 * @author Manoj SP
 */
public interface UinHistoryRepo extends JpaRepository<UinHistory, String> {
	/**
	 * Gets the uin by refId 
	 * 
	 * @param regId
	 * @return the Uin 
	 */
	@Query("select uinHash from UinHistory where regId = :regId")
	String getUinHashByRid(@Param("regId") String regId);
}
