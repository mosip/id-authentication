package io.mosip.kernel.masterdata.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.DeviceProviderHistory;

/**
 * The Interface DeviceProviderHistoryRepository.
 * 
 * @author Ramadurai Pandian
 * @since 1.0.0
 */
@Repository
public interface DeviceProviderHistoryRepository extends BaseRepository<DeviceProviderHistory, String> {

	/**
	 * Find by id and is active is true.
	 *
	 * @param id
	 *            the id
	 * @return the device provider
	 */
	@Query(value = "(select * from device_provider_h dph WHERE id = ?1 AND eff_dtimes<= ?2 and (is_deleted is null or is_deleted =false) ORDER BY eff_dtimes DESC) LIMIT 1 ", nativeQuery = true)
	DeviceProviderHistory findDeviceProviderHisByIdAndEffTimes(String id, LocalDateTime effTimes);

}
