package io.mosip.kernel.syncdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.syncdata.entity.MachineType;

/**
 * Repository function to fetching Machine Type details
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
@Repository
public interface MachineTypeRepository extends BaseRepository<MachineType, String> {

	@Query(value = "SELECT mt.code, mt.name, mt.descr, mt.lang_code, mt.is_active, mt.cr_by, mt.cr_dtimes, mt.upd_by, mt.upd_dtimes, mt.is_deleted, mt.del_dtimes FROM master.machine_type mt,master.machine_spec ms, master.machine_master mm WHERE mt.code= ms.mtyp_code and ms.id= mm.mspec_id and mm.id=?1", nativeQuery = true)
	List<MachineType> findAllByMachineId(String machineId);

	@Query(value = "SELECT mt.code, mt.name, mt.descr, mt.lang_code, mt.is_active, mt.cr_by, mt.cr_dtimes, mt.upd_by, mt.upd_dtimes, mt.is_deleted, mt.del_dtimes FROM master.machine_type mt,master.machine_spec ms, master.machine_master mm WHERE mt.code= ms.mtyp_code and ms.id= mm.mspec_id and mm.id=?1 and (mt.cr_dtimes > ?2 or mt.upd_dtimes > ?2 or mt.del_dtimes > ?2)", nativeQuery = true)
	List<MachineType> findLatestByMachineId(String machineId, LocalDateTime lastUpdated);
}
