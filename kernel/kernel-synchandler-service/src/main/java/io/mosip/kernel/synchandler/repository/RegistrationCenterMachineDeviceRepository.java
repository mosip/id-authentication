package io.mosip.kernel.synchandler.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.synchandler.entity.RegistrationCenterMachineDevice;
import io.mosip.kernel.synchandler.entity.id.RegistrationCenterMachineDeviceID;

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

	@Query("SELECT r.registrationCenterMachineDevicePk.regCenterId FROM RegistrationCenterMachineDevice r where r.registrationCenterMachineDevicePk.machineId =?1")
	List<String> findAllByMachineId(String machineId);

	@Query("SELECT r.registrationCenterMachineDevicePk.deviceId FROM RegistrationCenterMachineDevice r where r.registrationCenterMachineDevicePk.regCenterId =?1")
	List<String> findAllByRegistrationCenterId(String registrationCenterId);

}
