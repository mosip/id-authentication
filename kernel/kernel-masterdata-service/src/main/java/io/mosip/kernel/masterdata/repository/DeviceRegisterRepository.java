package io.mosip.kernel.masterdata.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.DeviceRegister;

@Repository
public interface DeviceRegisterRepository extends BaseRepository<DeviceRegister, String> {

	@Query("From DeviceRegister dr where dr.deviceCode=?1 And dr.statusCode is null")
	DeviceRegister findDeviceRegisterByCodeAndStatusCode(String code);
}
