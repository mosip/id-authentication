package io.mosip.kernel.syncdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.syncdata.entity.RegistrationCenterType;

/**
 * Interface for RegistrationCenterType Repository.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
public interface RegistrationCenterTypeRepository extends BaseRepository<RegistrationCenterType, String> {
	/**
	 * fetch all registration center types by code
	 * 
	 * @param code
	 * @return {@link List<RegistrationCenterType>}
	 */
	List<RegistrationCenterType> findAllByCode(String code);

	@Query(value = "SELECT regtype.code, regtype.name, regtype.descr, regtype.lang_code, regtype.is_active, regtype.cr_by, regtype.cr_dtimes, regtype.upd_by, regtype.upd_dtimes, regtype.is_deleted, regtype.del_dtimes FROM master.reg_center_type regtype , master.registration_center rc,master.reg_center_machine_device rcmd where regtype.code= rc.cntrtyp_code and rc.id=rcmd.regcntr_id and rcmd.machine_id= ?1", nativeQuery = true)
	List<RegistrationCenterType> findRegistrationCenterTypeByMachineId(String machineId);

	@Query(value = "SELECT regtype.code, regtype.name, regtype.descr, regtype.lang_code, regtype.is_active, regtype.cr_by, regtype.cr_dtimes, regtype.upd_by, regtype.upd_dtimes, regtype.is_deleted, regtype.del_dtimes FROM master.reg_center_type regtype , master.registration_center rc,master.reg_center_machine_device rcmd where regtype.code= rc.cntrtyp_code and rc.id=rcmd.regcntr_id and rcmd.machine_id= ?1 and (regtype.cr_dtimes > ?2 or regtype.upd_dtimes > ?2 or regtype.del_dtimes > ?2) ", nativeQuery = true)
	List<RegistrationCenterType> findLatestRegistrationCenterTypeByMachineId(String machineId,
			LocalDateTime lastUpdated);
}
