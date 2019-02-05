package io.mosip.kernel.syncdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.syncdata.entity.RegistrationCenterMachineDevice;
import io.mosip.kernel.syncdata.entity.id.RegistrationCenterMachineDeviceID;

/**
 * Repository to perform CRUD operations on RegistrationCenterMachineDevice.
 * 
 * @author Neha
 * @since 1.0.0
 * @see RegistrationCenterMachineDevice
 * @see BaseRepository
 *
 */
@Repository
public interface RegistrationCenterMachineDeviceRepository
		extends BaseRepository<RegistrationCenterMachineDevice, RegistrationCenterMachineDeviceID> {

	/**
	 * Method to fetch Devices id for which machine is mapped.
	 * 
	 * @param registrationCenterId
	 *            id of the registration center
	 * @return devices id
	 */
	@Query("FROM RegistrationCenterMachineDevice rcmd where rcmd.registrationCenterMachineDevicePk.regCenterId =?1")
	List<RegistrationCenterMachineDevice> findAllByRegistrationCenterId(String registrationCenterId);
	
	/**
	 * Method to fetch RegistrationCenterMachineDevice data for which registrationCenterId is mapped.
	 * 
	 * @param registrationCenterId
	 *            id of the registration center
	 * @return  RegistrationCenterMachineDevice list
	 */
	@Query("FROM RegistrationCenterMachineDevice rcmd where rcmd.registrationCenterMachineDevicePk.regCenterId =?1 AND (rcmd.createdDateTime > ?2 OR rcmd.updatedDateTime > ?2 OR rcmd.deletedDateTime > ?2)")
	List<RegistrationCenterMachineDevice> findAllByRegistrationCenterIdCreatedUpdatedDeleted(String registrationCenterId, LocalDateTime lastUpdated);

}
