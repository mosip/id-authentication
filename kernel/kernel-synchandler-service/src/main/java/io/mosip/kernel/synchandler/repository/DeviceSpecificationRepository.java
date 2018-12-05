package io.mosip.kernel.synchandler.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.synchandler.entity.DeviceSpecification;

/**
 * 
 * @author Uday Kumar
 * @since 1.0.0
 *
 */
@Repository
public interface DeviceSpecificationRepository extends BaseRepository<DeviceSpecification, String> {
	/**
	 * This method trigger query to fetch the Device specific detail for the given
	 * language code.
	 *
	 * @param langCode
	 *            languageCode provided by user
	 * 
	 * @return Device specific Details fetched from database
	 */

	List<DeviceSpecification> findByLangCodeAndIsDeletedFalse(String langcode);

	/**
	 * This method trigger query to fetch the Device specific detail for the given
	 * language code and device Type Code.
	 *
	 * @param langCode
	 *            LanguageCode provided by user
	 * @param deviceTypeCode
	 *            Device Type Code provided by user
	 * 
	 * @return Device specific Details fetched from database
	 */
	List<DeviceSpecification> findByLangCodeAndDeviceTypeCodeAndIsDeletedFalse(String languageCode,
			String deviceTypeCode);

	@Query(value = "SELECT ds.id, ds.name, ds.brand, ds.model, ds.dtyp_code, ds.min_driver_ver, ds.descr, ds.lang_code, ds.is_active, ds.cr_by, ds.cr_dtimes, ds.upd_by, ds.upd_dtimes, ds.is_deleted, ds.del_dtimes FROM master.device_spec ds  , master.device_master dm, master.reg_center_machine_device rcmd where  dm.dspec_id= ds.id and dm.id = rcmd.device_id and rcmd.machine_id = ?1", nativeQuery = true)
	List<DeviceSpecification> findDeviceTypeByMachineId(String machineId);

	@Query(value = "SELECT ds.id, ds.name, ds.brand, ds.model, ds.dtyp_code, ds.min_driver_ver, ds.descr, ds.lang_code, ds.is_active, ds.cr_by, ds.cr_dtimes, ds.upd_by, ds.upd_dtimes, ds.is_deleted, ds.del_dtimes FROM master.device_spec ds  , master.device_master dm, master.reg_center_machine_device rcmd where  dm.dspec_id= ds.id and dm.id = rcmd.device_id and rcmd.machine_id = ?1 and (ds.cr_dtimes > ?2 or ds.upd_dtimes > ?2 or ds.del_dtimes > ?2)", nativeQuery = true)
	List<DeviceSpecification> findlatestDeviceTypeByMachineId(String machineId, LocalDateTime lastUpdated);
}
