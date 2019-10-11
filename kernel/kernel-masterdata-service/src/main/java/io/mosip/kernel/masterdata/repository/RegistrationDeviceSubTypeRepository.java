package io.mosip.kernel.masterdata.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.RegistrationDeviceSubType;

/**
 * interface for RegistrationDeviceSubType Repository
 * @author Megha Tanga
 *
 */
@Repository
public interface RegistrationDeviceSubTypeRepository extends BaseRepository<RegistrationDeviceSubType, String> {
	
	@Query("FROM RegistrationDeviceSubType d where d.code=?1 AND (d.isDeleted is null OR d.isDeleted = false) AND d.isActive = true")
	RegistrationDeviceSubType findByCodeAndIsDeletedFalseorIsDeletedIsNullAndIsActiveTrue(String code);

}
