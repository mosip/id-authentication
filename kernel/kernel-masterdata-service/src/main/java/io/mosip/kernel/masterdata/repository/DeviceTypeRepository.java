package io.mosip.kernel.masterdata.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.DeviceType;

/**
 * Repository function to fetching Device Type details
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
@Repository
public interface DeviceTypeRepository extends BaseRepository<DeviceType, String> {
	
	@Query("FROM DeviceType d where (isDeleted is null OR isDeleted = false) AND isActive = true")
	List<DeviceType> findAllDeviceTypeByIsActiveAndIsDeletedFalseOrNull();

}