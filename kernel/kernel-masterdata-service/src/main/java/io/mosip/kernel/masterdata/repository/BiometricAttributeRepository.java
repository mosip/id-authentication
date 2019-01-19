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
	@Query("select b from BiometricAttribute b where b.biometricTypeCode = ?1 and b.langCode = ?2 and (b.isDeleted is null or b.isDeleted = false) ")
	List<BiometricAttribute> findByBiometricTypeCodeAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(
			String biometricTypeCode, String langCode);

}
