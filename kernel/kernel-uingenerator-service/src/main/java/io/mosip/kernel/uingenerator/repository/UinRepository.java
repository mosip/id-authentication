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
	 * Finds the number of free uins
	 * 
	 * @return the number of free uins
	 */
	public long countByUsedIsFalse();

	/**
	 * Finds an unused uin
	 * 
	 * @return an unused uin
	 */
	public UinEntity findFirstByUsedIsFalse();

}
