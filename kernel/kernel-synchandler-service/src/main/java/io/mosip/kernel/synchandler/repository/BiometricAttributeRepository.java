package io.mosip.kernel.synchandler.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.synchandler.entity.BiometricAttribute;

/**
 * 
 * @author Uday Kumar
 * @since 1.0.0
 *
 */
@Repository
public interface BiometricAttributeRepository extends BaseRepository<BiometricAttribute, String> {
	List<BiometricAttribute> findByBiometricTypeCodeAndLangCodeAndIsDeletedFalse(String biometricTypeCode, String langCode);
}
