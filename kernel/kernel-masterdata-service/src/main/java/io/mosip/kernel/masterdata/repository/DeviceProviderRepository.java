package io.mosip.kernel.masterdata.repository;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.DeviceProvider;

/**
 * The Interface DeviceProviderRepository.
 * 
 * @author Srinivasan
 * @since 1.0.0
 */
@Repository
public interface DeviceProviderRepository extends BaseRepository<DeviceProvider, String> {

	/**
	 * Find by id and is active is true.
	 *
	 * @param id
	 *            the id
	 * @return the device provider
	 */
	DeviceProvider findByIdAndIsActiveIsTrue(String id);
}
