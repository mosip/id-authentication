/**
 * 
 */
package io.mosip.kernel.masterdata.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.DeviceProviderHistory;
import io.mosip.kernel.masterdata.entity.RegisteredDeviceHistory;

/**
 * @author Ramadurai Pandian
 *
 */
@Repository
public interface RegisteredDeviceHistoryRepository extends BaseRepository<RegisteredDeviceHistory,String> {
	
	@Query(value="(select * from registered_device_master_h rdh WHERE code = ?1 AND eff_dtimes<= ?2 and (is_deleted is null or is_deleted =false) ORDER BY eff_dtimes DESC) LIMIT 1 ",nativeQuery=true)
	RegisteredDeviceHistory findRegisteredDeviceHistoryByIdAndEffTimes(String code,LocalDateTime effTimes);

}
