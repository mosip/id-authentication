package io.mosip.kernel.uingenerator.repository;

import java.time.LocalDateTime;

import javax.persistence.Column;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.uingenerator.entity.UinEntity;

/**
 * Repository having function to count free uins and find an unused uin
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Repository
public interface UinRepository extends JpaRepository<UinEntity, String> {

	/**
	 * Finds the number of free uins,
	 * 
	 * @param status status of the uin
	 * 
	 * @return the number of free uins
	 */
	public long countByStatus(String status);

	/**
	 * Finds an unused uin
	 * 
	 * @param status status of the uin
	 * 
	 * @return an unused uin
	 */
	@Query(value="select uu.uin, uu.cr_by, uu.cr_dtimes, uu.del_dtimes, uu.is_deleted, uu.upd_by, uu.upd_dtimes, uu.uin_status from kernel.uin uu where uu.uin_status=? limit 1",nativeQuery = true)
	public UinEntity findFirstByStatus(String status);
	

	/**
	 * find a UIN in pool
	 * 
	 * @param uin pass uin as param
	 * 
	 * @return an unused uin
	 */
	public UinEntity findByUin(String uin);

	@Modifying
	@Query(value="UPDATE kernel.uin SET uin_status=:status, upd_by=:contextUser, upd_dtimes=:uptimes where uin=:uin",nativeQuery = true)
	public void updateStatus(@Param("status") String status, @Param("contextUser") String contextUser, @Param("uptimes") LocalDateTime uptimes,@Param("uin")  String uin);
}
