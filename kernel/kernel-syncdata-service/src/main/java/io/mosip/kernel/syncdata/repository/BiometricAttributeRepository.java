package io.mosip.kernel.syncdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.syncdata.entity.BiometricAttribute;

/**
 * 
 * @author Uday Kumar
 * @since 1.0.0
 *
 */
@Repository
public interface BiometricAttributeRepository extends BaseRepository<BiometricAttribute, String> {
	List<BiometricAttribute> findByBiometricTypeCodeAndLangCodeAndIsDeletedFalse(String biometricTypeCode,
			String langCode);

	@Query("FROM BiometricAttribute WHERE createdDateTime > ?1 OR updatedDateTime > ?1  OR deletedDateTime > ?1")
	List<BiometricAttribute> findAllLatestCreatedUpdateDeleted(LocalDateTime lastUpdated);
}
