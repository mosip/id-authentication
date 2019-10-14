/**
 * 
 */
package io.mosip.kernel.masterdata.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.DeviceServiceHistory;

/**
 * @author Ramadurai Pandian
 *
 */
@Repository
public interface DeviceServiceHistoryRepository extends BaseRepository<DeviceServiceHistory,String>{
	
	/**
	 * Find by id and is active is true.
	 *
	 * @param id
	 *            the id
	 * @return the device service
	 */
	 @Query(value="(select * from mosip_device_service_h dsh id = ?1 and eff_dtimes<= ?2 and (is_deleted is null or is_deleted =false) ORDER BY eff_dtimes DESC) LIMIT 1",nativeQuery=true)
	 DeviceServiceHistory findByIdAndIsActiveIsTrueAndByEffectiveTimes(String id,LocalDateTime effiveTimes);

	 @Query(value="(select * from mosip_device_service_h dsh id = ?1 and dprovider_id=?2 and eff_dtimes<= ?3 and (is_deleted is null or is_deleted =false) ORDER BY eff_dtimes DESC) LIMIT 1",nativeQuery=true)
	 DeviceServiceHistory findByIdAndDProviderId(String id, String deviceProviderId,LocalDateTime effTimes);

}
