package io.mosip.kernel.masterdata.repository;

import org.springframework.data.jpa.repository.Query;
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
	
	@Query("FROM MachineSpecification m where m.id = ?1 and (m.isDeleted is null or m.isDeleted = false)")
	MachineSpecification findMachineSpecificationByIdAndIsDeletedFalseOrIsDeletedIsNull(String id);

}
