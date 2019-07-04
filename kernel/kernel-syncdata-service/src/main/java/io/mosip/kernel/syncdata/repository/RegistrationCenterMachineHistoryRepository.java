package io.mosip.kernel.syncdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import io.mosip.kernel.syncdata.entity.RegistrationCenterMachineHistory;
import io.mosip.kernel.syncdata.entity.id.RegistrationCenterMachineID;

/**
 * Repository to perform CRUD operations on RegistrationCenterMachineHistory.
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 * @see RegistrationCenterMachineHistory
 * @see JpaRepository
 *
 */
@Repository
public interface RegistrationCenterMachineHistoryRepository
		extends JpaRepository<RegistrationCenterMachineHistory, RegistrationCenterMachineID> {
	/**
	 * 
	 * @param regId            - registration center id
	 * @param lastUpdated      - last updated time
	 * @param currentTimeStamp - current timestamp
	 * @return list of {@link RegistrationCenterMachineHistory} - list of
	 *         registration center machine history
	 */
	@Query("FROM RegistrationCenterMachineHistory rcmh WHERE rcmh.registrationCenterMachineHistoryPk.regCenterId=?1 AND ((rcmh.createdDateTime > ?2 AND rcmh.createdDateTime<=?3) OR (rcmh.updatedDateTime > ?2 AND rcmh.updatedDateTime <=?3) OR (rcmh.deletedDateTime > ?2 AND rcmh.deletedDateTime<=?3))")
	List<RegistrationCenterMachineHistory> findLatestRegistrationCenterMachineHistory(String regId,
			LocalDateTime lastUpdated, LocalDateTime currentTimeStamp);
}
