package io.mosip.kernel.idgenerator.tspid.repository;

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
	@Query(value = "select t.curr_seq_no,t.cr_by,t.cr_dtimes,t.upd_by,t.upd_dtimes FROM master.tspid_seq t where t.curr_seq_no=(select max(t.curr_seq_no) FROM master.tspid_seq t)", nativeQuery = true)
	Tsp findLastTspId();

}
