package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.BiometricTypeData;
import io.mosip.kernel.masterdata.dto.BiometricTypeDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.BiometricTypeResponseDto;
import io.mosip.kernel.masterdata.entity.BiometricType;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;

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
	public BiometricTypeResponseDto getAllBiometricTypes();
	
	/**
	 * To fetch all biometric types using language code
	 * 
	 * @param langCode
	 		the language code
	 * @return {@linkplain BiometricTypeDto}
	 */
	public BiometricTypeResponseDto getAllBiometricTypesByLanguageCode(String langCode);

	/**
	 * To fetch biometric type using id and language code
	 * 
	 * @param code
	 * @param langCode
	 * @return {@linkplain BiometricType}
	 */
	public BiometricTypeResponseDto getBiometricTypeByCodeAndLangCode(String code, String langCode);
	
	public CodeAndLanguageCodeID addBiometricType(RequestDto<BiometricTypeData> biometricTypeRequestDto);
}
