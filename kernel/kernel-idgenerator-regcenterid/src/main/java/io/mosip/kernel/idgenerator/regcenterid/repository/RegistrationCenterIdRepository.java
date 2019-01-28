package io.mosip.kernel.idgenerator.regcenterid.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.Modifying;
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
	@Query(value = "select t.curr_seq_no,t.cr_by,t.cr_dtimes,t.upd_by,t.upd_dtimes FROM master.rcid_seq t ", nativeQuery = true)
	RegistrationCenterId findLastRCID();

	/**
	 * 
	 * Method to update Registration Center ID.
	 * 
	 * @param currentId
	 *            the current ID.
	 * @param previousId
	 *            the last ID.
	 * @param currentTime
	 *            the current time.
	 * @return the rows updated.
	 */
	@Modifying
	@Query("UPDATE RegistrationCenterId SET rcid=?1,updatedDateTime=?3,createdDateTime=?3 WHERE rcid=?2")
	int updateRCID(int currentId, int previousId, LocalDateTime currentTime);
}
