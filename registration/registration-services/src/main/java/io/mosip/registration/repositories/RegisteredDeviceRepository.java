package io.mosip.registration.repositories;

import java.util.List;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.RegisteredDevice;

@Repository
public interface RegisteredDeviceRepository extends BaseRepository<RegisteredDevice, String> {

	List<RegisteredDevice> findAllByIsActiveTrueAndDeviceId(String deviceId);
}
