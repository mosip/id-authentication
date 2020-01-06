package io.mosip.kernel.syncdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.syncdata.entity.DeviceSpecification;

/**
 * 
 * @author Uday Kumar
 * @since 1.0.0
 *
 */
@Repository
public interface DeviceSpecificationRepository extends JpaRepository<DeviceSpecification, String> {

	/**
	 * Method to find what are the device specifications for the devices which are
	 * mapped to a machine.
	 * 
	 * @param machineId id of the machine
	 * @return list of {@link DeviceSpecification} - list of device specification
	 */
	@Query(value = "SELECT distinct ds.id, ds.name, ds.brand, ds.model, ds.dtyp_code, ds.min_driver_ver, ds.descr, ds.lang_code, ds.is_active, ds.cr_by, ds.cr_dtimes, ds.upd_by, ds.upd_dtimes, ds.is_deleted, ds.del_dtimes FROM master.device_spec ds  , master.device_master dm, master.reg_center_machine_device rcmd where  dm.dspec_id= ds.id and dm.id = rcmd.device_id and rcmd.machine_id = ?1", nativeQuery = true)
	List<DeviceSpecification> findDeviceTypeByMachineId(String machineId);

	/**
	 * Method to find what are the newly created, updated deleted device
	 * specifications for the devices which are mapped to a machine after last
	 * updated timeStamp.
	 * 
	 * @param regCenterId      id of the registration center
	 * @param lastUpdated      timeStamp - last updated time stamp
	 * @param currentTimeStamp - currentTimestamp
	 * @return list of {@link DeviceSpecification} - list of device specification
	 */
	@Query(value = "SELECT ds.id, ds.name, ds.brand, ds.model, ds.dtyp_code, ds.min_driver_ver, ds.descr, ds.lang_code, ds.is_active, ds.cr_by, ds.cr_dtimes, ds.upd_by, ds.upd_dtimes, ds.is_deleted, ds.del_dtimes from master.device_spec ds where ds.id in (select distinct md.dspec_id from master.device_master md where md.id in(select distinct rcd.device_id from master.reg_center_device rcd where rcd.regcntr_id =?1)) and ((ds.cr_dtimes > ?2 and ds.cr_dtimes <=?3) or (ds.upd_dtimes > ?2 and ds.upd_dtimes<=?3) or (ds.del_dtimes > ?2 and ds.del_dtimes <=?3))", nativeQuery = true)
	List<DeviceSpecification> findLatestDeviceTypeByRegCenterId(String regCenterId, LocalDateTime lastUpdated,
			LocalDateTime currentTimeStamp);
}
