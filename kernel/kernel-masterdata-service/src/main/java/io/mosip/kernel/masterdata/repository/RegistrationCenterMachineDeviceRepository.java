package io.mosip.kernel.masterdata.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.RegistrationCenterMachineDevice;
import io.mosip.kernel.masterdata.entity.id.RegistrationCenterMachineDeviceID;

/**
 * Repository to perform CRUD operations on RegistrationCenterMachineDevice.
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 * @see RegistrationCenterMachineDevice
 * @see BaseRepository
 *
 */
@Repository
public interface RegistrationCenterMachineDeviceRepository
		extends BaseRepository<RegistrationCenterMachineDevice, RegistrationCenterMachineDeviceID> {

	@Modifying
	@Transactional
	@Query("UPDATE RegistrationCenterMachineDevice rcm SET rcm.isDeleted = true ,rcm.deletedDateTime = ?1 WHERE rcm.registrationCenterMachineDevicePk.regCenterId = ?2 AND rcm.registrationCenterMachineDevicePk.machineId = ?3 AND rcm.registrationCenterMachineDevicePk.deviceId = ?4")
	int deleteRegCenterMachineDevice(LocalDateTime deletedDateTime, String regId, String machineId, String deviceId);
	
	@Query("FROM RegistrationCenterMachineDevice rcm where rcm.registrationCenterMachineDevicePk.regCenterId = ?1 AND rcm.registrationCenterMachineDevicePk.machineId = ?3 AND rcm.registrationCenterMachineDevicePk.deviceId = ?2 AND (rcm.isDeleted is null or isDeleted=false)")
	RegistrationCenterMachineDevice findByIdAndIsDeletedFalseOrIsDeletedIsNull(String regId,String deviceId,String machineId);
}
