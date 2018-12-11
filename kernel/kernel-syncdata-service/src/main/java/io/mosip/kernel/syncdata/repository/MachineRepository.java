package io.mosip.kernel.syncdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.syncdata.entity.Machine;

/**
 * Repository function to fetching machine details
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */

@Repository
public interface MachineRepository extends BaseRepository<Machine, String> {
	/**
	 * This method trigger query to fetch the all Machine details code.
	 * 
	 * @return MachineDetail fetched from database
	 */
	List<Machine> findAllByIsDeletedFalse();

	/**
	 * This method trigger query to fetch the Machine detail for the given machine
	 * id and language code.
	 * 
	 * 
	 * @param Id
	 *            Machine Id provided by user
	 * @param langCode
	 *            languageCode provided by user
	 * @return MachineDetail fetched from database
	 */

	Machine findAllByIdAndLangCodeAndIsDeletedFalse(String id, String langCode);

	/**
	 * This method trigger query to fetch the Machine detail for the given language
	 * code.
	 * 
	 * @param Id
	 *            Machine Id provided by user
	 * 
	 * @return MachineDetail fetched from database
	 */

	List<Machine> findAllByLangCodeAndIsDeletedFalse(String langCode);

	@Query(value = "SELECT mm.id, mm.name, mm.mac_address, mm.serial_num, mm.ip_address, mm.mspec_id, mm.lang_code, mm.is_active, mm.cr_by, mm.cr_dtimes, mm.upd_by, mm.upd_dtimes, mm.is_deleted, mm.del_dtimes, mm.validity_end_dtimes FROM master.machine_master mm where mm.id=?1 and (mm.cr_dtimes > ?2 or mm.upd_dtimes > ?2 or mm.del_dtimes > ?2)", nativeQuery = true)
	List<Machine> findAllLatestCreatedUpdateDeleted(String id, LocalDateTime lastUpdated);

	@Query(value = "SELECT mm.id, mm.name, mm.mac_address, mm.serial_num, mm.ip_address, mm.mspec_id, mm.lang_code, mm.is_active, mm.cr_by, mm.cr_dtimes, mm.upd_by, mm.upd_dtimes, mm.is_deleted, mm.del_dtimes, mm.validity_end_dtimes FROM master.machine_master mm where mm.id=?1", nativeQuery = true)
	List<Machine> findMachineById(String machineId);
}
