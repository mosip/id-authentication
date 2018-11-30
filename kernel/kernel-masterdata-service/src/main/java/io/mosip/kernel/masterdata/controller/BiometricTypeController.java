package io.mosip.kernel.masterdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.BiometricTypeData;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.BiometricTypeResponseDto;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.service.BiometricTypeService;

/**
 * Controller APIs to get Biometric types details
 * 
 * @author Neha
 * @since 1.0.0
 *
 */
@RestController
@RequestMapping("/biometrictypes")
public class BiometricTypeController {

	@Autowired
	private BiometricTypeService biometricTypeService;

	/**
	 * API to fetch all Biometric types details
	 * 
	 * @return All Biometric types
	 */
	@GetMapping
	public BiometricTypeResponseDto fetchAllBioMetricType() {
		return biometricTypeService.getAllBiometricTypes();
	}

	/**
	 * API to fetch all Biometric types details based on language code
	 * 
	 * @return All Biometric types of specific language
	 */
	@GetMapping("/{langcode}")
	public BiometricTypeResponseDto fetchAllBiometricTypeUsingLangCode(@PathVariable("langcode") String langCode) {
		return biometricTypeService.getAllBiometricTypesByLanguageCode(langCode);
	}

	/**
	 * API to fetch a Biometric type details using id and language code
	 * 
	 * @return A Biometric type
	 */
	@GetMapping("/{code}/{langcode}")
	public BiometricTypeResponseDto fetchBiometricTypeUsingCodeAndLangCode(@PathVariable("code") String code,
			@PathVariable("langcode") String langCode) {
		return biometricTypeService.getBiometricTypeByCodeAndLangCode(code, langCode);
	}
	
	@PostMapping
	public CodeAndLanguageCodeID addBiometricType(@RequestBody RequestDto<BiometricTypeData> biometricType) {
		return biometricTypeService.addBiometricType(biometricType);
	}
}
