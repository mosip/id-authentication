package io.mosip.kernel.syncdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.syncdata.entity.RegistrationCenterDevice;
import io.mosip.kernel.syncdata.entity.id.RegistrationCenterDeviceID;

/**
 * Repository to perform CRUD operations on RegistrationCenterDevice.
 * 
 * @author Neha
 * @since 1.0.0
 * @see RegistrationCenterDevice
 * @see BaseRepository
 *
 */
@Repository
public interface RegistrationCenterDeviceRepository
		extends BaseRepository<RegistrationCenterDevice, RegistrationCenterDeviceID> {

	@Query(value = "FROM RegistrationCenterDevice rd WHERE rd.registrationCenterDevicePk.regCenterId =?1 ")
	List<RegistrationCenterDevice> findAllByRegistrationCenter(String registrationCenterId);

	@Query(value = "FROM RegistrationCenterDevice rd WHERE rd.registrationCenterDevicePk.regCenterId =?1 AND (rd.createdDateTime > ?2 OR rd.updatedDateTime > ?2 OR rd.deletedDateTime > ?2)")
	List<RegistrationCenterDevice> findAllLatestByRegistrationCenterCreatedUpdatedDeleted(String regId,
			LocalDateTime lastUpdated);

}
