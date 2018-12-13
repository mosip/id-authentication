package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.BiometricTypeData;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.BiometricTypeResponseDto;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;

/**
 * Service APIs to get Biometric types details
 * 
 * @author Neha
 * @since 1.0.0
 */
public interface BiometricTypeService {

	/**
	 * Method to fetch all Biometric Type details
	 * 
	 * @return BiometricTypeResponseDto
	 * 
	 * @throws MasterDataServiceException
	 *             If fails to fetch required Biometric Type
	 * 
	 * @throws DataNotFoundException
	 *             If given required Biometric Type not found
	 */
	public BiometricTypeResponseDto getAllBiometricTypes();
	
	/**
	 * Method to fetch all Biometric Type details based on language code
	 * 
	 * @param langCode
	 *            The language code
	 * 
	 * @return BiometricTypeResponseDto
	 * 
	 * @throws MasterDataServiceException
	 *             If fails to fetch required Biometric Type
	 * 
	 * @throws DataNotFoundException
	 *             If given required Biometric Type not found
	 */
	public BiometricTypeResponseDto getAllBiometricTypesByLanguageCode(String langCode);

	/**
	 * Method to fetch all Biometric Type details based on id and language code
	 * 
	 * @param code
	 *            The id of Biometric Type
	 * 
	 * @param langCode
	 *            The language code
	 * 
	 * @return BiometricTypeResponseDto
	 * 
	 * @throws MasterDataServiceException
	 *             If fails to fetch required Biometric Type
	 * 
	 * @throws DataNotFoundException
	 *             If given required Biometric Type not found
	 */
	public BiometricTypeResponseDto getBiometricTypeByCodeAndLangCode(String code, String langCode);
	
	/**
	 * Method to create a Biometric Type
	 * 
	 * @param biometricTypeRequestDto
	 *            The Biometric Type data
	 * 
	 * @return {@link CodeAndLanguageCodeID}
	 * 
	 * @throws MasterDataServiceException
	 *             If fails to insert the Biometric Type
	 */
	public CodeAndLanguageCodeID addBiometricType(RequestDto<BiometricTypeData> biometricTypeRequestDto);
}
