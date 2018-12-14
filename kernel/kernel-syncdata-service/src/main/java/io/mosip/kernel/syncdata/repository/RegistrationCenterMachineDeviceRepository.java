package io.mosip.kernel.syncdata.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.syncdata.entity.RegistrationCenterMachineDevice;
import io.mosip.kernel.syncdata.entity.id.RegistrationCenterMachineDeviceID;

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

	/**
	 * Method to fetch Registration Center id for which machine is mapped.
	 * 
	 * @param machineId
	 *            id of the machine
	 * @return registration center id
	 */
	@Query("SELECT r.registrationCenterMachineDevicePk.regCenterId FROM RegistrationCenterMachineDevice r where r.registrationCenterMachineDevicePk.machineId =?1")
	List<String> findAllByMachineId(String machineId);

	/**
	 * Method to fetch Devices id for which machine is mapped.
	 * 
	 * @param registrationCenterId
	 *            id of the registration center
	 * @return devices id
	 */
	@Query("SELECT r.registrationCenterMachineDevicePk.deviceId FROM RegistrationCenterMachineDevice r where r.registrationCenterMachineDevicePk.regCenterId =?1")
	List<String> findAllByRegistrationCenterId(String registrationCenterId);

}
