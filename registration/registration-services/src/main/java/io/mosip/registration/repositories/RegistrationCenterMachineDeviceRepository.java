package io.mosip.registration.repositories;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.RegCentreMachineDevice;
import io.mosip.registration.entity.id.RegCentreMachineDeviceId;

/**
 * This repository interface for {@link RegCentreMachineDevice} entity
 * 
 * @author Brahmananda Reddy
 * @since 1.0.0
 *
 */
public interface RegistrationCenterMachineDeviceRepository extends BaseRepository<RegCentreMachineDevice, RegCentreMachineDeviceId> {

}
