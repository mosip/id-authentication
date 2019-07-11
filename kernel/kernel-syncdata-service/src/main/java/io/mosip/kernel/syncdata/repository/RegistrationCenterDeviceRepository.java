package io.mosip.kernel.syncdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import io.mosip.kernel.syncdata.entity.RegistrationCenterDevice;
import io.mosip.kernel.syncdata.entity.id.RegistrationCenterDeviceID;

/**
 * Repository to perform CRUD operations on RegistrationCenterDevice.
 * 
 * @author Neha
 * @since 1.0.0
 * @see RegistrationCenterDevice
 * @see JpaRepository
 *
 */
@Repository
public interface RegistrationCenterDeviceRepository
		extends JpaRepository<RegistrationCenterDevice, RegistrationCenterDeviceID> {
	/**
	 * 
	 * @param registrationCenterId - registration center id
	 * @return list of {@link RegistrationCenterDevice} -list of registration center
	 *         device
	 */
	@Query(value = "FROM RegistrationCenterDevice rd WHERE rd.registrationCenterDevicePk.regCenterId =?1 ")
	List<RegistrationCenterDevice> findAllByRegistrationCenter(String registrationCenterId);

	/**
	 * 
	 * @param regId            - registration center id
	 * @param lastUpdated      - last updated time stamp
	 * @param currentTimeStamp - current time stamp
	 * @return list of {@link RegistrationCenterDevice} -list of registration center
	 *         device
	 */
	@Query(value = "FROM RegistrationCenterDevice rd WHERE rd.registrationCenterDevicePk.regCenterId =?1 AND ((rd.createdDateTime > ?2 AND rd.createdDateTime<=?3) OR (rd.updatedDateTime > ?2 AND rd.updatedDateTime <=?3) OR (rd.deletedDateTime > ?2 AND rd.deletedDateTime<=?3))")
	List<RegistrationCenterDevice> findAllLatestByRegistrationCenterCreatedUpdatedDeleted(String regId,
			LocalDateTime lastUpdated, LocalDateTime currentTimeStamp);

}
