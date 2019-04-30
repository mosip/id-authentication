package io.mosip.idrepository.vid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.mosip.idrepository.vid.entity.Vid;

/**
 * 
 * @author Prem Kumar
 *
 */
public interface VidRepo extends JpaRepository<Vid, String>{
	
	@Query("select vidTypeCode from Vid where vid = :vid")
	String retrieveVidTypeCode(@Param("vid")String vid);

	@Query("select uinHash from Vid where vid = :vid")
	String retrieveUinByVid(String vid);

}
