package io.mosip.kernel.syncdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import io.mosip.kernel.syncdata.entity.RegistrationCenterUserMachine;
import io.mosip.kernel.syncdata.entity.id.RegistrationCenterMachineUserID;

/**
 * Repository class for center user machine mapping
 * 
 * @author Neha
 * @since 1.0.0
 *
 */
@Repository
public interface RegistrationCenterUserMachineRepository
		extends JpaRepository<RegistrationCenterUserMachine, RegistrationCenterMachineUserID> {
	/**
	 * 
	 * @param regId            - registration center id
	 * @param lastUpdated      - last updated time stamp
	 * @param currentTimeStamp - current time stamp
	 * @return list of {@link RegistrationCenterUserMachine} - list of registration
	 *         center user machine
	 * 
	 */
	@Query("FROM RegistrationCenterUserMachine rcum WHERE rcum.registrationCenterMachineUserID.cntrId=?1 AND ((rcum.createdDateTime > ?2 AND rcum.createdDateTime<=?3) OR (rcum.updatedDateTime > ?2 AND rcum.updatedDateTime <=?3) OR (rcum.deletedDateTime > ?2 AND rcum.deletedDateTime<=?3))")
	List<RegistrationCenterUserMachine> findAllByRegistrationCenterIdCreatedUpdatedDeleted(String regId,
			LocalDateTime lastUpdated, LocalDateTime currentTimeStamp);

	/**
	 * 
	 * @param regId - registation center id
	 * @return list of {@link RegistrationCenterUserMachine} - list of registration
	 *         center user machine
	 */
	@Query("FROM RegistrationCenterUserMachine rcum WHERE rcum.registrationCenterMachineUserID.cntrId=?1")
	List<RegistrationCenterUserMachine> findAllByRegistrationCenterId(String regId);

}
