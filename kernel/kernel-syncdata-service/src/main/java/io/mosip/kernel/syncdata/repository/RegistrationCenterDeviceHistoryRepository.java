package io.mosip.kernel.syncdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import io.mosip.kernel.syncdata.entity.RegistrationCenterDeviceHistory;
import io.mosip.kernel.syncdata.entity.RegistrationCenterDeviceHistoryPk;

/**
 * Repository to perform CRUD operations on RegistrationCenterDeviceHistory.
 * 
 * @author Bal Vikash Sharma
 * @author Uday Kumar
 * @since 1.0.0
 * @see RegistrationCenterDeviceHistory
 * @see JpaRepository
 *
 */
@Repository
public interface RegistrationCenterDeviceHistoryRepository
		extends JpaRepository<RegistrationCenterDeviceHistory, RegistrationCenterDeviceHistoryPk> {

	/**
	 * 
	 * @param regId            - registration center id
	 * @param lastUpdated      - last updated time
	 * @param currentTimeStamp - current time stamp
	 * @return list of {@link RegistrationCenterDeviceHistory} -list of registration
	 *         center device history
	 */
	@Query("FROM RegistrationCenterDeviceHistory rcdh WHERE rcdh.registrationCenterDeviceHistoryPk.regCenterId=?1 AND ((rcdh.createdDateTime > ?2 AND rcdh.createdDateTime<=?3) OR (rcdh.updatedDateTime > ?2 AND rcdh.updatedDateTime <=?3) OR (rcdh.deletedDateTime > ?2 AND rcdh.deletedDateTime<=?3))")
	List<RegistrationCenterDeviceHistory> findLatestRegistrationCenterDeviceHistory(String regId,
			LocalDateTime lastUpdated, LocalDateTime currentTimeStamp);

}
