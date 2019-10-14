package io.mosip.kernel.masterdata.repository;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.MOSIPDeviceService;

/**
 * MDS repository class.
 *
 * @author Srinivasan
 * @author Megha Tanga
 */
@Repository
public interface MOSIPDeviceServiceRepository extends BaseRepository<MOSIPDeviceService, String> {

	/**
	 * Find by id and is active is true.
	 *
	 * @param id
	 *            the id
	 * @return the device service
	 */
	MOSIPDeviceService findByIdAndIsActiveIsTrue(String id);

	/**
	 * Find by provider id and service version.
	 *
	 * @param id
	 *            the id
	 * @param providerId
	 *            the provider id
	 * @return the device service
	 */
	MOSIPDeviceService findByIdAndDeviceProviderId(String id, String deviceProviderId);
}
