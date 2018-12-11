package io.mosip.kernel.syncdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.syncdata.entity.BiometricType;

/**
 * @author Neha
 * @since 1.0.0
 *
 */

@Repository
public interface BiometricTypeRepository extends BaseRepository<BiometricType, String> {

	/**
	 * Get all Biometric types
	 *
	 * @return {@link List<BiometricType>}
	 */
	public List<BiometricType> findAllByIsDeletedFalse(Class<BiometricType> entityClass);

	/**
	 * Get all Biometric types of a specific language using language code
	 * 
	 * @param langCode
	 * @return {@link List<BiometricType>}
	 */
	List<BiometricType> findAllByLangCodeAndIsDeletedFalse(String langCode);

	/**
	 * Get Biometric type by specific id and language code
	 * 
	 * @param code
	 * @param langCode
	 * @return {@linkplain BiometricType}
	 */
	BiometricType findByCodeAndLangCodeAndIsDeletedFalse(String code, String langCode);
	@Query("FROM BiometricType WHERE createdDateTime > ?1 OR updatedDateTime > ?1  OR deletedDateTime > ?1")
	List<BiometricType> findAllLatestCreatedUpdateDeleted(LocalDateTime lastUpdated);
}
