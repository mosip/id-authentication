package io.mosip.kernel.idgenerator.tokenid.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.idgenerator.tokenid.entity.TokenId;

@Repository
public interface TokenIdRepository extends BaseRepository<TokenId, String> {
	/**
	 * This method fetch random value and counter value from database.
	 * 
	 * @return the list of entity.
	 */
	@Query("from TokenId")
	List<TokenId> findRandomValues();

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
	@Query("UPDATE TokenId p SET p.sequenceCounter=?1 WHERE p.randomValue=?2")
	int updateCounterValue(String sequenceCounter, String randomValue);
}
