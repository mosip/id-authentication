package io.mosip.kernel.masterdata.repository;

import java.time.LocalDateTime;
import java.util.List;

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

	@Query("FROM RegistrationCenterMachineDevice rcm where rcm.registrationCenterMachineDevicePk.regCenterId = ?1 AND rcm.registrationCenterMachineDevicePk.machineId = ?3 AND rcm.registrationCenterMachineDevicePk.deviceId = ?2 AND (rcm.isDeleted is null or rcm.isDeleted=false) AND rcm.isActive = true")
	RegistrationCenterMachineDevice findByIdAndIsDeletedFalseOrIsDeletedIsNull(String regId, String deviceId,
			String machineId);

	@Query("FROM RegistrationCenterMachineDevice rcm where rcm.registrationCenterMachineDevicePk.machineId = ?1 AND (rcm.isDeleted is null or rcm.isDeleted=false) and rcm.isActive = true")
	List<RegistrationCenterMachineDevice> findByMachineIdAndIsDeletedFalseOrIsDeletedIsNull(String machineId);

	@Query("FROM RegistrationCenterMachineDevice rcm where rcm.registrationCenterMachineDevicePk.deviceId = ?1 AND (rcm.isDeleted is null or rcm.isDeleted=false) and rcm.isActive = true")
	List<RegistrationCenterMachineDevice> findByDeviceIdAndIsDeletedFalseOrIsDeletedIsNull(String deviceId);

	@Query("FROM RegistrationCenterMachineDevice rcm where rcm.registrationCenterMachineDevicePk.regCenterId = ?1 AND (rcm.isDeleted is null or rcm.isDeleted=false) and rcm.isActive = true")
	List<RegistrationCenterMachineDevice> findByRegCenterIdAndIsDeletedFalseOrIsDeletedIsNull(String regCenterId);
}
