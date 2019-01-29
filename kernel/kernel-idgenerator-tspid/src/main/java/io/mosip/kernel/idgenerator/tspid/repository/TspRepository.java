package io.mosip.kernel.idgenerator.tspid.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.idgenerator.tspid.entity.Tsp;

/**
 * Repository class for fetching and updating tspid.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Repository
public interface TspRepository extends BaseRepository<Tsp, Integer> {

	/**
	 * Method to fetch last updated tspid.
	 * 
	 * @return the entity.
	 */
	@Query(value = "select t.curr_seq_no,t.cr_by,t.cr_dtimes,t.upd_by,t.upd_dtimes FROM master.tspid_seq t ", nativeQuery = true)
	Tsp findLastTspId();

	/**
	 * 
	 * Method to update TSP ID.
	 * 
	 * @param currentId
	 *            the current ID.
	 * @param previousId
	 *            the last ID.
	 * @param updateTime
	 *            the current time.
	 * @return the rows updated.
	 */
	@Modifying
	@Query("UPDATE Tsp SET tspId=?1,updatedDateTime=?3,createdDateTime=?3 WHERE tspId=?2")
	int updateTspId(int currentId, int previousId, LocalDateTime updateTime);
}
