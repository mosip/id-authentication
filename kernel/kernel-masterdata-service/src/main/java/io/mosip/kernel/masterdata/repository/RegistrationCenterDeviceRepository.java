package io.mosip.kernel.masterdata.repository;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.RegistrationCenterDevice;
import io.mosip.kernel.masterdata.entity.id.RegistrationCenterDeviceID;

/**
 * Repository to perform CRUD operations on RegistrationCenterDevice.
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 * @see RegistrationCenterDevice
 * @see BaseRepository
 *
 */
@Repository
public interface RegistrationCenterDeviceRepository
		extends BaseRepository<RegistrationCenterDevice, RegistrationCenterDeviceID> {
}
