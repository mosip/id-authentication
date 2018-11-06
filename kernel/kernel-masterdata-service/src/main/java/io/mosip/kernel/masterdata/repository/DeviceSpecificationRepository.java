package io.mosip.kernel.masterdata.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.DeviceSpecification;

/**
 * 
 * @author Uday Kumar
 * @since 1.0.0
 *
 */
@Repository
public interface DeviceSpecificationRepository extends BaseRepository<DeviceSpecification, String> {
	List<DeviceSpecification> findByLangCode(String languageCode);

	List<DeviceSpecification> findByLangCodeAndDeviceTypeCode(String languageCode, String deviceTypeCode);
}
