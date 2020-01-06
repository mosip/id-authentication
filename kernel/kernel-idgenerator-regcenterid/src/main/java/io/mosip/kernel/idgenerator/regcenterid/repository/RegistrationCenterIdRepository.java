package io.mosip.kernel.idgenerator.regcenterid.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.idgenerator.regcenterid.entity.RegistrationCenterId;

/**
 * Repository class for {@link RegistrationCenterId}
 * 
 * @author Sagar Mahaptra
 * @since 1.0.0
 *
 */
@Repository
public interface RegistrationCenterIdRepository extends BaseRepository<RegistrationCenterId, Integer> {

	/**
	 * Method to find last upatded RCID.
	 * 
	 * @return the entity.
	 */
	@Query(value = "select t.curr_seq_no,t.cr_by,t.cr_dtimes,t.upd_by,t.upd_dtimes FROM master.rcid_seq t where t.curr_seq_no=(select max(t.curr_seq_no) FROM master.rcid_seq t)", nativeQuery = true)
	RegistrationCenterId findLastRCID();

}
