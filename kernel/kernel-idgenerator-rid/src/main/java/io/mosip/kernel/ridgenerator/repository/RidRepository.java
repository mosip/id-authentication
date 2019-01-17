package io.mosip.kernel.ridgenerator.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.ridgenerator.entity.Rid;

/**
 * Rid Generator repository.
 * 
 * @author Ritesh Sinha
 * @author Abhishek Kumar
 * @since 1.0.0
 *
 */
@Repository
public interface RidRepository extends BaseRepository<Rid, Integer> {
	/**
	 * Method to fetch last updated sequence no.
	 * 
	 * @return the entity.
	 */
	@Query(value = "SELECT r.currentSequenceNo FROM Rid r")
	Rid findLastRid();

	/**
	 * Method to update previous sequence no.
	 * 
	 * @param currentId
	 *            the current sequence no.
	 * @param previousId
	 *            the previous sequence no.
	 * @return the number of rows updated.
	 */
	@Modifying
	@Query("UPDATE Tsp SET currentSequenceNo=?1 WHERE currentSequenceNo=?2")
	int updateRid(int currentId, int previousId);
}
