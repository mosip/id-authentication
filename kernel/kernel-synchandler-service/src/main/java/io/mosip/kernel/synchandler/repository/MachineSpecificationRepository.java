package io.mosip.kernel.synchandler.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.synchandler.entity.MachineSpecification;

@Repository
public interface MachineSpecificationRepository extends BaseRepository<MachineSpecification, String> {

	@Query(value = "SELECT ms.id, ms.name, ms.brand, ms.model, ms.mtyp_code, ms.min_driver_ver, ms.descr, ms.lang_code, ms.is_active, ms.cr_by, ms.cr_dtimes, ms.upd_by, ms.upd_dtimes, ms.is_deleted, ms.del_dtimes FROM master.machine_spec ms, master.machine_master mm WHERE ms.id= mm.mspec_id and mm.id=?1", nativeQuery = true)
	List<MachineSpecification> findByMachineId(String machineId);

	@Query(value = "SELECT ms.id, ms.name, ms.brand, ms.model, ms.mtyp_code, ms.min_driver_ver, ms.descr, ms.lang_code, ms.is_active, ms.cr_by, ms.cr_dtimes, ms.upd_by, ms.upd_dtimes, ms.is_deleted, ms.del_dtimes FROM master.machine_spec ms, master.machine_master mm WHERE ms.id= mm.mspec_id and mm.id=?1 and (ms.cr_dtimes > ?2 or ms.upd_dtimes > ?2 or ms.del_dtimes > ?2)", nativeQuery = true)
	List<MachineSpecification> findLatestByMachineId(String machineId, LocalDateTime lastUpdated);
}
