package io.mosip.kernel.idgenerator.partnerid.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.idgenerator.partnerid.entity.Partner;

/**
 * Repository class for create partner id.
 * 
 * @author Uday kumar
 * @since 1.0.0
 *
 */
// TODO: once the db is finalize changes required according to schema given
@Repository
public interface PartnerRepository extends BaseRepository<Partner, Integer> {

	/**
	 * Method to fetch last updated partner id.
	 * 
	 * @return the entity.
	 */
	@Query(value = "select t.curr_seq_no,t.cr_by,t.cr_dtimes,t.upd_by,t.upd_dtimes FROM master.tspid_seq t where t.curr_seq_no=(select max(t.curr_seq_no) FROM master.tspid_seq t)", nativeQuery = true)
	Partner findLastTspId();
 
}
