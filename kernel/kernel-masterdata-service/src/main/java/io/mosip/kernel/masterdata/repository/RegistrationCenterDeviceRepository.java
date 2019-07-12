package io.mosip.kernel.masterdata.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.RegistrationCenterDevice;
import io.mosip.kernel.masterdata.entity.id.RegistrationCenterDeviceID;

/**
 * Repository to perform CRUD operations on RegistrationCenterDevice.
 * 
 * @author Dharmesh Khandelwal
 * @author Bal Vikash Sharma
 * @since 1.0.0
 * @see RegistrationCenterDevice
 * @see BaseRepository
 *
 */
@Repository
public interface RegistrationCenterDeviceRepository
		extends BaseRepository<RegistrationCenterDevice, RegistrationCenterDeviceID> {

	@Query("FROM RegistrationCenterDevice WHERE registrationCenterDevicePk =?1 and (isDeleted is null or isDeleted =false) and isActive = true")
	Optional<RegistrationCenterDevice> findAllNondeletedMappings(RegistrationCenterDeviceID registrationCenterDevicePk);

	@Query("FROM RegistrationCenterDevice rd where rd.registrationCenterDevicePk.deviceId = ?1 AND (rd.isDeleted is null or rd.isDeleted=false) and rd.isActive = true")
	List<RegistrationCenterDevice> findByDeviceIdAndIsDeletedFalseOrIsDeletedIsNull(String deviceId);

	@Query("FROM RegistrationCenterDevice rd where rd.registrationCenterDevicePk.regCenterId = ?1 AND (rd.isDeleted is null or rd.isDeleted=false) and rd.isActive = true")
	List<RegistrationCenterDevice> findByRegCenterIdAndIsDeletedFalseOrIsDeletedIsNull(String regCenterId);
	
	@Query(value="select count(*) from master.reg_center_device where regcntr_id=?1 and (is_deleted is null or is_deleted=false);",nativeQuery=true)
	Long countCenterDevices(String centerId); 

}
