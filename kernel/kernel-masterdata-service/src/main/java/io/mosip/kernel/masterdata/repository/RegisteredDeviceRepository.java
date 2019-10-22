package io.mosip.kernel.masterdata.repository;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.RegisteredDevice;


/**
 * The Interface RegisteredDeviceRepository.
 * @author Srinivasan
 */
@Repository
public interface RegisteredDeviceRepository extends BaseRepository<RegisteredDevice, String> {

	/**
	 * Find by code and is active is true.
	 *
	 * @param deviceCode the device code
	 * @return the registered device
	 */
	RegisteredDevice findByCodeAndIsActiveIsTrue(String deviceCode);
}
