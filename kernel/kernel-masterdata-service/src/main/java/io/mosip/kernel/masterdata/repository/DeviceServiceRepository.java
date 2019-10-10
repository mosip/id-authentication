package io.mosip.kernel.masterdata.repository;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.DeviceService;

/**
 * MDS repository class.
 *
 * @author Srinivasan
 */
@Repository
public interface DeviceServiceRepository extends BaseRepository<DeviceService, String> {

	/**
	 * Find by id and is active is true.
	 *
	 * @param id
	 *            the id
	 * @return the device service
	 */
	DeviceService findByIdAndIsActiveIsTrue(String id);

	/**
	 * Find by provider id and service version.
	 *
	 * @param id the id
	 * @param providerId            the provider id
	 * @return the device service
	 */
	DeviceService findByIdAndDProviderId(String id, String providerId);
}
