package io.mosip.kernel.masterdata.repository;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.MachineSpecification;

/**
 * Repository to perform CRUD operations on MachineSpecification.
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */

@Repository
public interface MachineSpecificationRepository extends BaseRepository<MachineSpecification, String> {
	
	//@Query("FROM MachineSpecification m where m.id = ?1 and (m.isDeleted is null or m.isDeleted = false)")
	//@Query(value ="select m.id, m.cr_by , m.cr_dtimes,m.del_dtimes ,m.is_active,m.is_deleted , m.upd_by ,m.upd_dtimes , m.brand , m.descr,m.mtyp_code ,m.min_driver_ver , m.model , m.name  from  master.machine_spec m  where  m.id=?1 and (m.is_deleted = false or m.is_deleted  is null) )",nativeQuery = true)
	//@Query(value = "select m.id, m.lang_code, m.cr_by , m.cr_dtimes, m.del_dtimes , m.is_active, m.is_deleted , m.upd_by , m.upd_dtimes , m.brand , m.descr, m.mtyp_code , m.min_driver_ver , m.model , m.name from master.machine_spec m where m.id=?1 and (m.is_deleted = false or m.is_deleted is null)",nativeQuery = true )
	//MachineSpecification findMachineSpecificationByIdAndIsDeletedFalseOrIsDeletedIsNull(String id);

}
