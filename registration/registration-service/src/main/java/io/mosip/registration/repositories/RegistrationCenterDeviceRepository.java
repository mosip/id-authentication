package io.mosip.registration.repositories;

import java.sql.Timestamp;
import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.RegCenterDevice;
import io.mosip.registration.entity.RegCenterDeviceId;

/**
 * This repository interface for {@link RegCenterDevice} entity
 * 
 * @author Brahmananda Reddy
 * @since 1.0.0
 *
 */
public interface RegistrationCenterDeviceRepository extends BaseRepository<RegCenterDevice, RegCenterDeviceId> {

	List<RegCenterDevice> findByRegCenterDeviceIdRegCenterIdAndIsActiveTrueAndRegDeviceMasterValidityEndDtimesGreaterThan(
			String centerId, Timestamp timestamp);


}
