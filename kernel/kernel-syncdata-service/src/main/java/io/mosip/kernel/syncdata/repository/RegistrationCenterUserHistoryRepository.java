package io.mosip.kernel.syncdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;

import org.springframework.data.jpa.repository.JpaRepository;
import io.mosip.kernel.syncdata.entity.RegistrationCenterUserHistory;
import io.mosip.kernel.syncdata.entity.id.RegistrationCenterUserHistoryID;

public interface RegistrationCenterUserHistoryRepository
		extends JpaRepository<RegistrationCenterUserHistory, RegistrationCenterUserHistoryID> {

	/**
	 * 
	 * @param regId            - registration center id
	 * @param lastUpdated      - last updated time stamp
	 * @param currentTimeStamp - current time stamp
	 * @return list of {@link RegistrationCenterUserHistory} - list of registration
	 *         center user history
	 */
	@Query("FROM RegistrationCenterUserHistory rcuh WHERE rcuh.regCntrId=?1 AND ((rcuh.createdDateTime > ?2 AND rcuh.createdDateTime<=?3) OR (rcuh.updatedDateTime > ?2 AND rcuh.updatedDateTime <=?3) OR (rcuh.deletedDateTime > ?2 AND rcuh.deletedDateTime<=?3))")
	List<RegistrationCenterUserHistory> findLatestRegistrationCenterUserHistory(String regId, LocalDateTime lastUpdated,
			LocalDateTime currentTimeStamp);
}
