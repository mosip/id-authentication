package io.mosip.registration.repositories;

import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.MachineMaster;
import io.mosip.registration.entity.RegCentreMachineDevice;
import io.mosip.registration.entity.RegCentreMachineDeviceId;
import io.mosip.registration.entity.RegistrationCenter;

/**
 * This repository interface for {@link RegCentreMachineDevice} entity
 * 
 * @author Brahmananda Reddy
 * @since 1.0.0
 *
 */
public interface RegistrationCenterMachineDeviceRepository
		extends BaseRepository<RegCentreMachineDevice, RegCentreMachineDeviceId> {
	
	/**
	 * Finds the {@link RegCentreMachineDevice} based on center Id and machine Id
	 * 
	 * @param centerId
	 *            the id of the {@link RegistrationCenter}
	 * @param machineId
	 *            the id of the {@link MachineMaster}
	 * @return the list of devices mapped to the given centerId and machineId
	 */
	List<RegCentreMachineDevice> findByRegCentreMachineDeviceIdRegCentreIdAndRegCentreMachineDeviceIdMachineId(
			String centerId, String machineId);
	
}
