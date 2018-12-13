package io.mosip.kernel.masterdata.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.BiometricType;

/**
 * @author Neha
 * @author Bal Vikash Sharma
 * @since 1.0.0
 *
 */

@Repository
public interface BiometricTypeRepository extends BaseRepository<BiometricType, String> {

	/**
	 * Get all Biometric types
	 * 
	 * @param entityClass
	 *            class of type {@link BiometricType}
	 * @return list of {@link BiometricType}
	 */
	public List<BiometricType> findAllByIsDeletedFalse(Class<BiometricType> entityClass);

	/**
	 * Get all Biometric types of a specific language using language code
	 * 
	 * @param langCode
	 *            is of type {@link String}
	 * @return list of {@link BiometricType}
	 */
	List<BiometricType> findAllByLangCodeAndIsDeletedFalse(String langCode);

	/**
	 * Get Biometric type by specific id and language code
	 * 
	 * @param code
	 *            biometric type code
	 * @param langCode
	 *            is of type {@link String}
	 * @return object of {@link BiometricType}
	 */
	BiometricType findByCodeAndLangCodeAndIsDeletedFalse(String code, String langCode);

}
