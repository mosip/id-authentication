package io.mosip.kernel.masterdata.repository;

import java.util.List;

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
	/**
	 * This method trigger query to fetch the all Machine details.
	 * 
	 * @return List MachineDetail fetched from database
	 * 
	 */
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

	@Query("FROM Machine m where m.id = ?1 and m.langCode = ?2 and (m.isDeleted is null or m.isDeleted = false)")
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

	@Query("FROM Machine m where m.langCode = ?1 and (m.isDeleted is null or m.isDeleted = false)")
	List<Machine> findAllByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(String langCode);

}
