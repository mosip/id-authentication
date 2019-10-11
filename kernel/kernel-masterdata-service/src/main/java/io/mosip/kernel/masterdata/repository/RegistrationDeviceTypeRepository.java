package io.mosip.kernel.masterdata.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.RegistrationDeviceType;

/**
 * Inerface for RegistrationDeviceType Repository
 * 
 * @author Megha Tanga
 *
 */
@Repository
public interface RegistrationDeviceTypeRepository extends BaseRepository<RegistrationDeviceType, String>{
	
	@Query("FROM RegistrationDeviceType d where d.code=?1 AND (d.isDeleted is null OR d.isDeleted = false) AND d.isActive = true")
	RegistrationDeviceType findByCodeAndIsDeletedFalseorIsDeletedIsNullAndIsActiveTrue(String code);

}
