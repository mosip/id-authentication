package io.mosip.kernel.masterdata.service;

import java.util.List;

import io.mosip.kernel.masterdata.dto.BioTypeCodeAndLangCodeAndAttributeCode;
import io.mosip.kernel.masterdata.dto.BiometricAttributeDto;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;

/**
 * 
 * @author Uday kumar
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
	 * @return {@link List<BiometricAttributeDTO>}}
	 */
	List<BiometricAttributeDto> getBiometricAttribute(String biometricTypeCode, String langCode);

	/**
	 * Function to save Device Specification Details to the Database
	 * 
	 * @param deviceTypes
	 * 
	 * @return {@link BioTypeCodeAndLangCodeAndAttributeCode}
	 */
	CodeAndLanguageCodeID createBiometricAttribute(BiometricAttributeDto biometricAttribute);

}
