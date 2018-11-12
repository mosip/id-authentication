package io.mosip.kernel.masterdata.service;

import java.util.List;

import io.mosip.kernel.masterdata.dto.BiometricTypeDto;
import io.mosip.kernel.masterdata.entity.BiometricType;

/**
 * @author Neha
 * @since 1.0.0
 */
public interface BiometricTypeService {

	/**
	 * To fetch all biometric types
	 * 
	 * @return {@linkplain BiometricTypeDto}
	 */
	List<BiometricTypeDto> getAllBiometricTypes();
	
	/**
	 * To fetch all biometric types using language code
	 * 
	 * @param langCode
	 		the language code
	 * @return {@linkplain BiometricTypeDto}
	 */
	List<BiometricTypeDto> getAllBiometricTypesByLanguageCode(String langCode);

	/**
	 * To fetch biometric type using id and language code
	 * 
	 * @param code
	 * @param langCode
	 * @return {@linkplain BiometricType}
	 */
	BiometricTypeDto getBiometricTypeByCodeAndLangCode(String code, String langCode);
}
