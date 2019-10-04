package io.mosip.kernel.syncdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.syncdata.entity.DeviceType;

/**
 * Repository function to fetching Device Type details
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
@Repository
public interface DeviceTypeRepository extends JpaRepository<DeviceType, String> {
	/**
	 * Method to find what are the device type the devices which are mapped to a
	 * machine.
	 * 
	 * @param machineId id of the machine
	 * @return list of {@link DeviceType} - list of device type
	 */
	@Query(value = "SELECT distinct dt.code, dt.name, dt.descr, dt.lang_code, dt.is_active, dt.cr_by, dt.cr_dtimes, dt.upd_by, dt.upd_dtimes, dt.is_deleted, dt.del_dtimes from  master.device_type dt, master.device_spec ds ,master.device_master dm, master.reg_center_machine_device rcmd where dt.code = ds.dtyp_code and dm.dspec_id = ds.id and dm.id= rcmd.device_id and rcmd.machine_id = ?1   ", nativeQuery = true)
	List<DeviceType> findDeviceTypeByMachineId(String machineId);

	/**
	 * Method to find what are the newly created, updated deleted device type for
	 * the devices which are mapped to a machine after last updated timeStamp.
	 * 
	 * @param regCenterId      id of the registration center
	 * @param lastUpdated      timeStamp - last updated time stamp
	 * @param currentTimeStamp - currentTimestamp
	 * @return list of {@link DeviceType} - list of device type
	 */
	@Query(value = "SELECT dt.code, dt.name, dt.descr, dt.lang_code, dt.is_active, dt.cr_by, dt.cr_dtimes, dt.upd_by, dt.upd_dtimes, dt.is_deleted, dt.del_dtimes from master.device_type dt where dt.code in (select distinct ds.dtyp_code from master.device_spec ds where ds.id in (select distinct md.dspec_id from master.device_master md where md.id in(select distinct rcd.device_id from master.reg_center_device rcd where rcd.regcntr_id=?1))) and ((dt.cr_dtimes > ?2 and dt.cr_dtimes <=?3) or (dt.upd_dtimes > ?2 and dt.upd_dtimes<=?3)  or (dt.del_dtimes > ?2 and dt.del_dtimes<=?3 )) ", nativeQuery = true)
	List<DeviceType> findLatestDeviceTypeByRegCenterId(String regCenterId, LocalDateTime lastUpdated,
			LocalDateTime currentTimeStamp);
}