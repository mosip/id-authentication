package io.mosip.idrepository.vid.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.mosip.idrepository.vid.entity.Vid;

/**
 * The Repository for Vid Entity
 * 
 * @author Prem Kumar
 *
 */
public interface VidRepo extends JpaRepository<Vid, String> {
	/**
	 * This Method is used to retrieve Vid Object.
	 * 
	 * @param vid
	 * @return Vid Object
	 */
	Vid findByVid(String vid);
	
	List<Vid> findByUinHashAndStatusCodeAndVidTypeCodeAndExpiryDTimesAfter(String uinHash, String statusCode, String vidTypeCode, LocalDateTime currentTime);
	
	/**
	 * The Query to retrieve Uin by passing vid as parameter.
	 * 
	 * @param vid
	 * @return String Uin
	 */
	@Query("select uin from Vid where vid = :vid")
	public String retrieveUinByVid(@Param("vid") String vid);

}
