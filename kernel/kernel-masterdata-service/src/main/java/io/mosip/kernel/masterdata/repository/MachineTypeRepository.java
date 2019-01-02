package io.mosip.kernel.masterdata.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.MachineType;

/**
 * Repository to perform CRUD operations on MachineType.
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
@Repository
public interface MachineTypeRepository extends BaseRepository<MachineType, String> {
	
	@Query("FROM MachineType m where m.code = ?1 and m.langCode =?2 and (m.isDeleted = true)")
	MachineType findMachineTypeByIdAndByLangCodeIsDeletedtrue(String code, String langCode );

}
