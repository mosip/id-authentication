package io.mosip.registration.repositories;

import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.BiometricAttribute;

/**
 * Interface for {@link BiometricAttribute} 
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */
public interface BiometricAttributeRepository extends BaseRepository<BiometricAttribute, String> {
	
	List<BiometricAttribute> findByLangCodeAndBiometricTypeCodeIn(String langCode, List<String> biometricType);

}
