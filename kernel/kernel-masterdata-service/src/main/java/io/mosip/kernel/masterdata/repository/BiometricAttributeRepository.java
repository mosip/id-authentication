package io.mosip.kernel.masterdata.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.BiometricAttribute;

/**
 * 
 * @author Uday Kumar
 * @since 1.0.0
 *
 */
@Repository
public interface BiometricAttributeRepository extends BaseRepository<BiometricAttribute, String> {
	public static final String GET_BIOMETRIC_TYPE_BY_BIO_TYPE_CODE_AND_LANG_CODE_IS_DELETED_FALSE_AND_IS_NULL = "select b from BiometricAttribute b where b.biometricTypeCode = ?1 and b.langCode = ?2 and (b.isDeleted is null or b.isDeleted = false) ";

	@Query(GET_BIOMETRIC_TYPE_BY_BIO_TYPE_CODE_AND_LANG_CODE_IS_DELETED_FALSE_AND_IS_NULL)
	List<BiometricAttribute> findByBiometricTypeCodeAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(
			String biometricTypeCode, String langCode);

}
