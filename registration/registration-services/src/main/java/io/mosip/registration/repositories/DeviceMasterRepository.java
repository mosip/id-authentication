package io.mosip.registration.repositories;

import java.sql.Timestamp;
import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.RegDeviceMaster;
import io.mosip.registration.entity.id.RegMachineSpecId;

/**
 * Repository class for common methods
 * 
 * @author Dinesh Ashokan
 * @since 1.0.0
 *
 */
public interface DeviceMasterRepository extends BaseRepository<RegDeviceMaster, RegMachineSpecId> {

	/**
	 * Find the device based on serial number.
	 *
	 * @param serialNo the serial no
	 * @param deviceType the device type
	 * @param currentDate the current date
	 * @return returns the record
	 */
	Long countBySerialNumAndNameAndIsActiveTrueAndValidityEndDtimesGreaterThan(String serialNo, String deviceType,
			Timestamp currentDate);

	/**
	 * Find all the devices mapped to the registration center by language code.
	 *
	 * @param langCode            the language code of the device
	 * @return list of all devices mapped to the registration center in the given
	 *         language
	 */
	List<RegDeviceMaster> findByRegMachineSpecIdLangCode(String langCode);
	
}	
