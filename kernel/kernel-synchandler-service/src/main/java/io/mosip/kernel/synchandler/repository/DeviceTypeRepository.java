package io.mosip.kernel.synchandler.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.synchandler.entity.DeviceType;

/**
 * Repository function to fetching Device Type details
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
@Repository
public interface DeviceTypeRepository extends BaseRepository<DeviceType, String> {
	@Query(value = "SELECT dt.code, dt.name, dt.descr, dt.lang_code, dt.is_active, dt.cr_by, dt.cr_dtimes, dt.upd_by, dt.upd_dtimes, dt.is_deleted, dt.del_dtimes from  master.device_type dt, master.device_spec ds ,master.device_master dm, master.reg_center_machine_device rcmd where dt.code = ds.dtyp_code and dm.dspec_id = ds.id and dm.id= rcmd.device_id and rcmd.machine_id = ?1   ", nativeQuery = true)
	List<DeviceType> findDeviceTypeByMachineId(String machineId);

	@Query(value = "SELECT dt.code, dt.name, dt.descr, dt.lang_code, dt.is_active, dt.cr_by, dt.cr_dtimes, dt.upd_by, dt.upd_dtimes, dt.is_deleted, dt.del_dtimes from  master.device_type dt, master.device_spec ds ,master.device_master dm, master.reg_center_machine_device rcmd where dt.code = ds.dtyp_code and dm.dspec_id = ds.id and dm.id= rcmd.device_id and rcmd.machine_id = ?1 and (dt.cr_dtimes > ?2 or dt.upd_dtimes > ?2 or dt.del_dtimes > ?2)  ", nativeQuery = true)
	List<DeviceType> findLatestDeviceTypeByMachineId(String machineId, LocalDateTime lastUpdated);
}