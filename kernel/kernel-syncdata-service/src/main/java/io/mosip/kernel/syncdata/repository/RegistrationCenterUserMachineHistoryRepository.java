package io.mosip.kernel.syncdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import io.mosip.kernel.syncdata.entity.RegistrationCenterUserMachineHistory;
import io.mosip.kernel.syncdata.entity.id.RegistrationCenterMachineUserHistoryID;

/**
 * Repository class for user machine mapping
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Repository
public interface RegistrationCenterUserMachineHistoryRepository
		extends JpaRepository<RegistrationCenterUserMachineHistory, RegistrationCenterMachineUserHistoryID> {

	/**
	 * 
	 * @param regId            - registration center id
	 * @param lastUpdated      - last updated time stamp
	 * @param currentTimeStamp - current time stamp
	 * @return list of {@link RegistrationCenterUserMachineHistory} - list of
	 *         registration center user machine history
	 */
	@Query("FROM RegistrationCenterUserMachineHistory rcumh WHERE rcumh.cntrId=?1 AND ((rcumh.createdDateTime > ?2 AND rcumh.createdDateTime<=?3) OR (rcumh.updatedDateTime > ?2 AND rcumh.updatedDateTime <=?3) OR (rcumh.deletedDateTime > ?2 AND rcumh.deletedDateTime<=?3))")
	List<RegistrationCenterUserMachineHistory> findLatestRegistrationCenterUserMachineHistory(String regId,
			LocalDateTime lastUpdated, LocalDateTime currentTimeStamp);
}
