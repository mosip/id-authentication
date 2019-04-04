package io.mosip.kernel.uingenerator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
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
	public UinEntity findFirstByStatus(String status);

	/**
	 * find a UIN in pool
	 * 
	 * @param uin pass uin as param
	 * 
	 * @return an unused uin
	 */
	public UinEntity findByUin(String uin);
}
