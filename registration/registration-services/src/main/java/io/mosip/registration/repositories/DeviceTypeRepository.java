package io.mosip.registration.repositories;

import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.RegDeviceType;
import io.mosip.registration.entity.RegDeviceTypeId;

/**
 * This repository interface for {@link RegDeviceType} entity
 * 
 * @author Brahmananda Reddy
 * @since 1.0.0
 *
 */
public interface DeviceTypeRepository extends BaseRepository<RegDeviceType, RegDeviceTypeId> {

	/**
	 * Fetches all active device types
	 * 
	 * @return the list of all active device types
	 */
	List<RegDeviceType> findByIsActiveTrue();

}
