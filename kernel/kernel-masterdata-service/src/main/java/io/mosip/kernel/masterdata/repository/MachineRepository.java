package io.mosip.kernel.masterdata.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.Machine;

/**
 * Repository to perform CRUD operations on Machine.
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */

@Repository
public interface MachineRepository extends BaseRepository<Machine, String> {
	// PagingAndSortingRepository<Machine, Integer>
	/**
	 * This method trigger query to fetch the all Machine details.
	 * 
	 * @return List MachineDetail fetched from database
	 * 
	 */
	@Query("FROM Machine where (isDeleted is null OR isDeleted = false) AND isActive = true")
	List<Machine> findAllByIsDeletedFalseOrIsDeletedIsNull();

	/**
	 * This method trigger query to fetch the Machine detail for the given machine
	 * id and language code.
	 * 
	 * 
	 * @param id
	 *            Machine Id provided by user
	 * @param langCode
	 *            language code provided by user
	 * @return List MachineDetail fetched from database
	 */

	@Query("FROM Machine m where m.id = ?1 and m.langCode = ?2 and (m.isDeleted is null or m.isDeleted = false) and m.isActive = true")
	List<Machine> findAllByIdAndLangCodeAndIsDeletedFalseorIsDeletedIsNull(String id, String langCode);

	/**
	 * This method trigger query to fetch the Machine detail for the given language
	 * code.
	 * 
	 * @param langCode
	 *            langCode provided by user
	 * 
	 * @return List MachineDetail fetched from database
	 */
	@Query("FROM Machine m where m.langCode = ?1 and (m.isDeleted is null or m.isDeleted = false) and m.isActive = true")
	List<Machine> findAllByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(String langCode);

	/**
	 * This method trigger query to fetch the Machine detail for the given id code.
	 * 
	 * @param id
	 *            machine Id provided by user
	 * 
	 * @return MachineDetail fetched from database
	 */

	@Query("FROM Machine m where m.id = ?1 and (m.isDeleted is null or m.isDeleted = false) and m.isActive = true")
	List<Machine> findMachineByIdAndIsDeletedFalseorIsDeletedIsNull(String id);

	/**
	 * This method trigger query to fetch the Machine detail for the given id code.
	 * 
	 * @param machineSpecId
	 *            machineSpecId provided by user
	 * 
	 * @return MachineDetail fetched from database
	 */

	@Query("FROM Machine m where m.machineSpecId = ?1 and (m.isDeleted is null or m.isDeleted = false) and m.isActive = true")
	List<Machine> findMachineBymachineSpecIdAndIsDeletedFalseorIsDeletedIsNull(String machineSpecId);

	/**
	 * This method trigger query to fetch the Machine detail for the given id and
	 * language code.
	 * 
	 * @param id
	 *            machine Id provided by user
	 * @param langCode
	 *            machine language code by user
	 * 
	 * @return MachineDetail fetched from database
	 */

	@Query("FROM Machine m where m.id = ?1 and m.langCode = ?2 and (m.isDeleted is null or m.isDeleted = false) AND m.isActive = true")
	Machine findMachineByIdAndLangCodeAndIsDeletedFalseorIsDeletedIsNull(String id, String langCode);

	/**
	 * This method trigger query to fetch the Machine detail those are mapped with
	 * the given regCenterId
	 * 
	 * @param regCenterId
	 *            regCenterId provided by user
	 * @return Machine fetch the list of Machine details those are mapped with the
	 *         given regCenterId
	 */
	@Query(value = "SELECT mm.id, mm.name, mm.mac_address, mm.serial_num, mm.ip_address, mm.mspec_id, mm.lang_code, mm.is_active,mm.validity_end_dtimes, mm.cr_by, mm.cr_dtimes, mm.upd_by, mm.upd_dtimes, mm.is_deleted, mm.del_dtimes FROM master.machine_master mm inner join master.reg_center_machine rcm on mm.id = rcm.machine_id where rcm.regcntr_id =?1", countQuery = "SELECT count(*) FROM  master.machine_master mm inner join master.reg_center_machine rcm on mm.id = rcm.machine_id where rcm.regcntr_id =?1", nativeQuery = true)
	Page<Machine> findMachineByRegCenterId(String regCenterId, Pageable pageable);

}
