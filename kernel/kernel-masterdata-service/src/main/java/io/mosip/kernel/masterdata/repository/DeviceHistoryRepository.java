package io.mosip.kernel.masterdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.DeviceHistory;

/**
 * Repository function to fetching device History Details
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
@Repository
public interface DeviceHistoryRepository extends BaseRepository<DeviceHistory, String> {

	/**
	 * This method trigger query to fetch Device History Details based on Device
	 * History Id, language code and effective date time
	 * 
	 * @param id
	 *            Device History id provided by user
	 * @param langCode
	 *            language code provided by user
	 * @param effectDtimes
	 *            effective Date and time provided by user in the format
	 *            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
	 * @return List Device History Details fetched from database
	 */

	@Query(value = "Select 	d.eff_dtimes, d.id,d.cr_by, d.cr_dtimes, d.del_dtimes, d.is_active, d.is_deleted, d.upd_by, d.upd_dtimes, d.ip_address, d.lang_code, d.mac_address, d.dspec_id, d.name,  d.serial_num, d.validity_end_dtimes from master.device_master_h d where d.id = ?1 and d.lang_code = ?2 and d.eff_dtimes <= ?3 and ( d.is_deleted = false or d.is_deleted is null) order by d.eff_dtimes desc limit 1", nativeQuery = true)
	List<DeviceHistory> findByFirstByIdAndLangCodeAndEffectDtimesLessThanEqualAndIsDeletedFalseOrIsDeletedIsNull(
			String id, String langCode, LocalDateTime effectDtimes);
}
