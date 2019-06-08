package io.mosip.kernel.syncdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import io.mosip.kernel.syncdata.entity.Device;

/**
 * Repository function to fetching device details
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */

@Repository
public interface DeviceRepository extends JpaRepository<Device, String> {
	/**
	 * Find list of devices mapped to a machine by machine id.
	 * 
	 * @param machineId id of machine
	 * @return list of {@link Device} - list of device
	 */
	@Query(value = "SELECT distinct dm.id, dm.name, dm.mac_address, dm.serial_num, dm.ip_address, dm.dspec_id, dm.lang_code, dm.is_active, dm.cr_by, dm.cr_dtimes, dm.upd_by, dm.upd_dtimes, dm.is_deleted, dm.del_dtimes, dm.validity_end_dtimes FROM master.device_master dm, master.reg_center_machine_device rcmd where dm.id = rcmd.device_id  and rcmd.machine_id = ?1", nativeQuery = true)
	List<Device> findDeviceByMachineId(String machineId);

	/**
	 * Find the recently created,updated,deleted list of devices mapped to a machine
	 * by machine id.
	 * 
	 * @param regCenterId      id of registration center
	 * @param lastUpdated      timeStamp - last updated time stamp
	 * @param currentTimeStamp - currentTimestamp
	 * @return list of {@link Device} - list of device
	 */
	@Query(value = "SELECT dm.id, dm.name, dm.mac_address, dm.serial_num, dm.ip_address, dm.dspec_id, dm.lang_code, dm.is_active, dm.cr_by, dm.cr_dtimes, dm.upd_by, dm.upd_dtimes, dm.is_deleted, dm.del_dtimes, dm.validity_end_dtimes from master.device_master dm where dm.id in(select distinct rcd.device_id from master.reg_center_device rcd where rcd.regcntr_id =?1) and ((dm.cr_dtimes > ?2 and dm.cr_dtimes <=?3 )or (dm.upd_dtimes > ?2 and dm.upd_dtimes <=?3) or (dm.del_dtimes > ?2 and dm.del_dtimes <=?3))", nativeQuery = true)
	List<Device> findLatestDevicesByRegCenterId(String regCenterId, LocalDateTime lastUpdated,
			LocalDateTime currentTimeStamp);
}
