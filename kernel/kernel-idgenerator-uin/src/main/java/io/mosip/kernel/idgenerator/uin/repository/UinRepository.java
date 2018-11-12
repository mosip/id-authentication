package io.mosip.kernel.idgenerator.uin.repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.idgenerator.uin.entity.UinEntity;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository having function to count free uins and find an unused uin
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Repository
public interface UinRepository extends BaseRepository<UinEntity, String> {

	/**
	 * Finds the number of free uins
	 * 
	 * @return the number of free uins
	 */
	@Query
	public int countFreeUin();

	/**
	 * Finds an unused uin
	 * 
	 * @return an unused uin
	 */
	@Query
	public UinEntity findUnusedUin();

}
