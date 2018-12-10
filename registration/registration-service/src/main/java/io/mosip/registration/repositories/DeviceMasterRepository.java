package io.mosip.registration.repositories;

import java.sql.Timestamp;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.RegDeviceMaster;

/**
 * Repository class for common methods
 * 
 * @author Dinesh Ashokan
 * @since 1.0.0
 *
 */
public interface DeviceMasterRepository extends BaseRepository<RegDeviceMaster, String>{
	
	/**
	 * Find the device based on serial number
	 *  
	 * @param deviceType
	 * @param serialNo
	 * @param currentDate
	 * @return
	 * 		returns the record 
	 */
	Long countBySerialNumberAndNameAndIsActiveTrueAndValidityEndDtimesGreaterThan(String deviceType,String  serialNo,Timestamp currentDate);
}
