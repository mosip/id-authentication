package io.mosip.kernel.idgenerator.prid.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.idgenerator.prid.entity.Prid;

/**
 * Repository for prid random values.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 */
@Repository
public interface PridRepository extends BaseRepository<Prid, String> {

	/**
	 * This method fetch random value and counter value from database.
	 * 
	 * @return the list of entity.
	 */
	@Query("from Prid")
	List<Prid> findRandomValues();

	/**
	 * This method update counter value in repository based on random value
	 * provided.
	 * 
	 * @param sequenceCounter
	 *            the current updated counter.
	 * @param randomValue
	 *            the random value exist in database.
	 * @return the number of rows affected.
	 */
	@Modifying
	@Query("UPDATE Prid p SET p.sequenceCounter=?1 WHERE p.randomValue=?2")
	int updateCounterValue(String sequenceCounter, String randomValue);
}
