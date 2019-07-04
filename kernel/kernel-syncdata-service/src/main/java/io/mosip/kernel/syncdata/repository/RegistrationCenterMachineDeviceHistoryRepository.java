package io.mosip.kernel.syncdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import io.mosip.kernel.syncdata.entity.RegistrationCenterMachineDeviceHistory;
import io.mosip.kernel.syncdata.entity.id.RegistrationCenterMachineDeviceID;

/**
 * Repository to perform CRUD operations on
 * RegistrationCenterMachineDeviceHistory.
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 * @see RegistrationCenterMachineDeviceHistory
 * @see JpaRepository
 *
 */
@Repository
public interface RegistrationCenterMachineDeviceHistoryRepository
		extends JpaRepository<RegistrationCenterMachineDeviceHistory, RegistrationCenterMachineDeviceID> {
	/**
	 * 
	 * @param regId            - registration center Id
	 * @param lastUpdated      - last updated time
	 * @param currentTimeStamp - current timestamp
	 * @return list of {@link RegistrationCenterMachineDeviceHistory} - list of
	 *         registration center machine device history
	 */
	@Query("FROM RegistrationCenterMachineDeviceHistory rcmdh WHERE rcmdh.registrationCenterMachineDeviceHistoryPk.regCenterId=?1 AND ((rcmdh.createdDateTime > ?2 AND rcmdh.createdDateTime<=?3) OR (rcmdh.updatedDateTime > ?2 AND rcmdh.updatedDateTime <=?3) OR (rcmdh.deletedDateTime > ?2 AND rcmdh.deletedDateTime<=?3))")
	List<RegistrationCenterMachineDeviceHistory> findLatestRegistrationCenterMachineDeviceHistory(String regId,
			LocalDateTime lastUpdated, LocalDateTime currentTimeStamp);
}
