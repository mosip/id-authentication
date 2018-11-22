package io.mosip.registration.repositories;

import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.DeviceType;
import io.mosip.registration.entity.RegDeviceTypeId;

/**
 * This repository interface for {@link DeviceType} entity
 * 
 * @author Brahmananda Reddy
 * @since 1.0.0
 *
 */
public interface DeviceTypeRepository extends BaseRepository<DeviceType, RegDeviceTypeId> {

	/**
	 * Fetches all active device types
	 * 
	 * @return the list of all active device types
	 */
	List<DeviceType> findByIsActiveTrue();

}
