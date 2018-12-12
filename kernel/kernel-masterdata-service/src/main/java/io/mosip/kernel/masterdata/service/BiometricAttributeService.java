package io.mosip.kernel.masterdata.service;

import java.util.List;

import io.mosip.kernel.masterdata.dto.BioTypeCodeAndLangCodeAndAttributeCode;
import io.mosip.kernel.masterdata.dto.BiometricAttributeDto;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;

/**
 * 
 * @author Uday
 * @since 1.0.0
 *
 */

public interface BiometricAttributeService {

	/**
	 * 
	 * methods to fetch list of biometricAttribute for given biometric type code and
	 * language code
	 * 
	 * @param biometricTypeCode
	 * @param langCode
	 * @return List of BiometricAttributeDto
	 */
	List<BiometricAttributeDto> getBiometricAttribute(String biometricTypeCode, String langCode);

	/**
	 * Function to save biometricAttribute Details to the Database
	 * 
	 * @param biometricAttribute
	 * 
	 * @return {@link BioTypeCodeAndLangCodeAndAttributeCode}
	 */
	CodeAndLanguageCodeID createBiometricAttribute(BiometricAttributeDto biometricAttribute);

}
