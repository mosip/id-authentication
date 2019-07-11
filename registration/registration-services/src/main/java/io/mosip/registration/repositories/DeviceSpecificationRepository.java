package io.mosip.registration.repositories;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.RegDeviceSpec;

/**
 * 
 * Repository function to fetching and save device specification details
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */
public interface DeviceSpecificationRepository extends BaseRepository<RegDeviceSpec, String> {

}
