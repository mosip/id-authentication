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
	 * Find list of devices mapped to a machine by machine id.
	 * 
	 * @param machineId
	 *            id of machine
	 * @return list of {@link Device}
	 */
	@Query(value = "SELECT dm.id, dm.name, dm.mac_address, dm.serial_num, dm.ip_address, dm.dspec_id, dm.lang_code, dm.is_active, dm.cr_by, dm.cr_dtimes, dm.upd_by, dm.upd_dtimes, dm.is_deleted, dm.del_dtimes, dm.validity_end_dtimes FROM master.device_master dm, master.reg_center_machine_device rcmd where dm.id = rcmd.device_id  and rcmd.machine_id = ?1", nativeQuery = true)
	List<Device> findDeviceByMachineId(String machineId);

	/**
	 * Find the recently created,updated,deleted list of devices mapped to a machine
	 * by machine id.
	 * 
	 * @param machineId
	 *            id of machine
	 * @param lastUpdated
	 *            timeStamp
	 * @return list of {@link Device}
	 */
	@Query(value = "SELECT dm.id, dm.name, dm.mac_address, dm.serial_num, dm.ip_address, dm.dspec_id, dm.lang_code, dm.is_active, dm.cr_by, dm.cr_dtimes, dm.upd_by, dm.upd_dtimes, dm.is_deleted, dm.del_dtimes, dm.validity_end_dtimes FROM master.device_master dm, master.reg_center_machine_device rcmd where dm.id = rcmd.device_id  and rcmd.machine_id = ?1 and (dm.cr_dtimes > ?2 or dm.upd_dtimes > ?2 or dm.del_dtimes > ?2) ", nativeQuery = true)
	List<Device> findLatestDevicesByMachineId(String machineId, LocalDateTime lastUpdated);
}
