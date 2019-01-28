package io.mosip.registration.repositories;

import java.sql.Timestamp;
import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.RegCenterDevice;
import io.mosip.registration.entity.RegCenterDeviceId;
import io.mosip.registration.entity.RegistrationCenter;

/**
 * This repository interface for {@link RegCenterDevice} entity
 * 
 * @author Brahmananda Reddy
 * @since 1.0.0
 *
 */
public interface RegistrationCenterDeviceRepository extends BaseRepository<RegCenterDevice, RegCenterDeviceId> {

	/**
	 * Fetches all valid devices associated or mapped to the given registration
	 * center
	 * 
	 * @param centerId
	 *            the id of the {@link RegistrationCenter}
	 * @param timestamp
	 *            the timestamp for validate the validatity end date of the device
	 * @return the list of valid devices associated with the given registration
	 *         center
	 */
	List<RegCenterDevice> findByRegCenterDeviceIdRegCenterIdAndIsActiveTrueAndRegDeviceMasterValidityEndDtimesGreaterThanEqual(
			String centerId, Timestamp timestamp);

}
