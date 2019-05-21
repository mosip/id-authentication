package io.mosip.registration.repositories;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.RegDeviceType;
import io.mosip.registration.entity.id.RegDeviceTypeId;

/**
 * This repository interface for {@link RegDeviceType} entity
 * 
 * @author Brahmananda Reddy
 * @since 1.0.0
 *
 */
public interface DeviceTypeRepository extends BaseRepository<RegDeviceType, RegDeviceTypeId> {

}
