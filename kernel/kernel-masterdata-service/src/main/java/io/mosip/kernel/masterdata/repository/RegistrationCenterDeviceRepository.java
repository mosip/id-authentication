package io.mosip.kernel.masterdata.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.RegistrationCenterDevice;
import io.mosip.kernel.masterdata.entity.id.RegistrationCenterDeviceID;

/**
 * Repository to perform CRUD operations on RegistrationCenterDevice.
 * 
 * @author Dharmesh Khandelwal
 * @author Bal Vikash Sharma
 * @since 1.0.0
 * @see RegistrationCenterDevice
 * @see BaseRepository
 *
 */
@Repository
public interface RegistrationCenterDeviceRepository
		extends BaseRepository<RegistrationCenterDevice, RegistrationCenterDeviceID> {
	
//	@Query("UPDATE RegistrationCenterDevice r SET r.isDeleted =true , r.deletedDateTime = ?2 WHERE r.registrationCenterDevicePk=?1 and(r.isDeleted is null or r.isDeleted =false)")
//	int deleteRegistrationCenterDeviceMapping(RegistrationCenterDeviceID registrationCenterDevicePk,LocalDateTime deletedDateTime);
}
