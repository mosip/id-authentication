package io.mosip.kernel.syncdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.syncdata.entity.Device;

/**
 * Repository function to fetching device details
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */

@Repository
public interface DeviceRepository extends BaseRepository<Device, String> {
	/**
	 * This method trigger query to fetch the Device detail for the given language
	 * code.
	 * 
	 * 
	 * @param langCode
	 *            languageCode provided by user
	 * 
	 * @return Device Details fetched from database
	 */

	List<Device> findByLangCodeAndIsDeletedFalse(String langCode);

	/**
	 * This method trigger query to fetch the Device detail for the given language
	 * code and Device Type code.
	 * 
	 * 
	 * @param langCode
	 *            languageCode provided by user
	 * @param deviceTypeCode
	 *            device Type Code provided by user
	 * @return Device Details fetched from database
	 * 
	 */
	@Query(value = "select d.id, d.name, d.mac_address, d.serial_num, d.ip_address, d.dspec_id, d.lang_code, d.is_active, s.dtyp_code from master.device_master  d, master.device_spec s where  d.dspec_id = s.id  and d.is_deleted = false  and  d.lang_code = ?1 and s.dtyp_code = ?2", nativeQuery = true)
	List<Object[]> findByLangCodeAndDtypeCode(String langCode, String deviceTypeCode);

	@Query(value = "SELECT dm.id, dm.name, dm.mac_address, dm.serial_num, dm.ip_address, dm.dspec_id, dm.lang_code, dm.is_active, dm.cr_by, dm.cr_dtimes, dm.upd_by, dm.upd_dtimes, dm.is_deleted, dm.del_dtimes, dm.validity_end_dtimes FROM master.device_master dm, master.reg_center_machine_device rcmd where dm.id = rcmd.device_id  and rcmd.machine_id = ?1", nativeQuery = true)
	List<Device> findDeviceByMachineId(String machineId);

	@Query(value = "SELECT dm.id, dm.name, dm.mac_address, dm.serial_num, dm.ip_address, dm.dspec_id, dm.lang_code, dm.is_active, dm.cr_by, dm.cr_dtimes, dm.upd_by, dm.upd_dtimes, dm.is_deleted, dm.del_dtimes, dm.validity_end_dtimes FROM master.device_master dm, master.reg_center_machine_device rcmd where dm.id = rcmd.device_id  and rcmd.machine_id = ?1 and (dm.cr_dtimes > ?2 or dm.upd_dtimes > ?2 or dm.del_dtimes > ?2) ", nativeQuery = true)
	List<Device> findLatestDevicesByMachineId(String machineId, LocalDateTime lastUpdated);
}
