package io.mosip.kernel.masterdata.repository;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.RegistrationCenterMachineDevice;
import io.mosip.kernel.masterdata.entity.RegistrationCenterMachineDevicePk;

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
		extends BaseRepository<RegistrationCenterMachineDevice, RegistrationCenterMachineDevicePk> {
}
