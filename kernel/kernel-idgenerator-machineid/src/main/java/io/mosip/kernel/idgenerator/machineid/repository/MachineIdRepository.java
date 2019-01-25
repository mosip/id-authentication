package io.mosip.kernel.idgenerator.machineid.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.Modifying;
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
	@Query(value = "select t.curr_seq_no,t.cr_by,t.cr_dtimes,t.upd_by,t.upd_dtimes FROM master.mid_seq t ", nativeQuery = true)
	MachineId findLastMID();

	/**
	 * 
	 * Method to update Machine ID.
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
	@Query("UPDATE MachineId SET mId=?1,updatedDateTime=?3,createdDateTime=?3 WHERE mId=?2")
	int updateMID(int currentId, int previousId, LocalDateTime updateTime);
}
