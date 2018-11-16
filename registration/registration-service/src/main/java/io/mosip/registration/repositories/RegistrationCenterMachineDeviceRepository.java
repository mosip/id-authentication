package io.mosip.registration.repositories;

import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.RegCentreMachineDevice;
import io.mosip.registration.entity.RegCentreMachineDeviceId;

/**
 * This repository interface for {@link RegCentreMachineDevice} entity
 * 
 * @author Brahmananda Reddy
 * @since 1.0.0
 *
 */
public interface RegistrationCenterMachineDeviceRepository
		extends BaseRepository<RegCentreMachineDevice, RegCentreMachineDeviceId> {
	List<RegCentreMachineDevice> findByRegCentreMachineDeviceIdRegCentreIdAndRegCentreMachineDeviceIdMachineId(
			String centerId, String machineId);

}
