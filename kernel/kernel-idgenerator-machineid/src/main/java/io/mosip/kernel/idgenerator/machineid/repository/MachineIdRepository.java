package io.mosip.kernel.idgenerator.machineid.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.idgenerator.machineid.entity.MachineId;

/**
 * Repository class for {@link MachineId}.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@Repository
public interface MachineIdRepository extends BaseRepository<MachineId, Integer> {

	/**
	 * Method to generate the last generate MID.
	 * 
	 * @return the MID entity response.
	 */
	@Query(value = "select t.curr_seq_no,t.cr_by,t.cr_dtimes,t.upd_by,t.upd_dtimes FROM master.mid_seq t where t.curr_seq_no=(select max(t.curr_seq_no) FROM master.mid_seq t)", nativeQuery = true)
	MachineId findLastMID();

}
