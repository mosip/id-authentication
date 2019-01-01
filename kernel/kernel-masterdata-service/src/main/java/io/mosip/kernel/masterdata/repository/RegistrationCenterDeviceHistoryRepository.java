package io.mosip.kernel.masterdata.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.RegistrationCenterDeviceHistory;
import io.mosip.kernel.masterdata.entity.RegistrationCenterDeviceHistoryPk;

/**
 * Repository to perform CRUD operations on RegistrationCenterDeviceHistory.
 * 
 * @author Bal Vikash Sharma
 * @author Uday Kumar
 * @since 1.0.0
 * @see RegistrationCenterDeviceHistory
 * @see BaseRepository
 *
 */
@Repository
public interface RegistrationCenterDeviceHistoryRepository
		extends BaseRepository<RegistrationCenterDeviceHistory, RegistrationCenterDeviceHistoryPk> {

	/**
	 * This method trigger query to fetch Registration center device History Details
	 * based on registration center id, device id,and effective date time
	 * 
	 * @param regCenterId
	 *            input Registration Center Id from User
	 * @param deviceId
	 *            input Device Id from user
	 * @param effectDtimes
	 *            effective Date and time provided by user in the format
	 *            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
	 * @return RegistrationCenterDeviceHistory fetched from database
	 */

	@Query(value = "Select rcdh.eff_dtimes, rcdh.regcntr_id,rcdh.cr_by, rcdh.cr_dtimes, rcdh.del_dtimes, rcdh.is_active, rcdh.is_deleted, rcdh.upd_by, rcdh.upd_dtimes, rcdh.device_id from master.reg_center_device_h rcdh where rcdh.regcntr_id = ?1 and rcdh.device_id = ?2 and rcdh.eff_dtimes <= ?3 and ( rcdh.is_deleted = false or rcdh.is_deleted is null) order by rcdh.eff_dtimes desc limit 1", nativeQuery = true)
	RegistrationCenterDeviceHistory findByFirstByRegCenterIdAndDeviceIdAndEffectDtimesLessThanEqualAndIsDeletedFalseOrIsDeletedIsNull(
			String regCenterId, String deviceId, LocalDateTime effectDtimes);
}
