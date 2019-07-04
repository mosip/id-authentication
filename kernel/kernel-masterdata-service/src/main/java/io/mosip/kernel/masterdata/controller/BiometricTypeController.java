package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.masterdata.dto.BiometricTypeDto;
import io.mosip.kernel.masterdata.dto.getresponse.BiometricTypeResponseDto;
import io.mosip.kernel.masterdata.entity.BiometricType;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.service.BiometricTypeService;
import io.swagger.annotations.Api;

/**
 * Controller APIs to get Biometric types details
 * 
 * @author Neha
 * @since 1.0.0
 *
 */
@RestController
@Api(tags = { "BiometricType" })
@RequestMapping("/biometrictypes")
public class BiometricTypeController {

	@Autowired
	private BiometricTypeService biometricTypeService;

	/**
	 * API to fetch all Biometric types details
	 * 
	 * @return All Biometric types
	 */
	@ResponseFilter
	@GetMapping
	@PreAuthorize("hasAnyRole('ZONAL_ADMIN','ZONAL_APPROVER')")
	public ResponseWrapper<BiometricTypeResponseDto> getAllBiometricTypes() {

		ResponseWrapper<BiometricTypeResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(biometricTypeService.getAllBiometricTypes());
		return responseWrapper;
	}

	/**
	 * API to fetch all Biometric types details based on language code
	 * 
	 * @param langCode The language code
	 * 
	 * @return All Biometric type details
	 */
	@ResponseFilter
	@GetMapping("/{langcode}")
	public ResponseWrapper<BiometricTypeResponseDto> getAllBiometricTypesByLanguageCode(
			@PathVariable("langcode") String langCode) {

		ResponseWrapper<BiometricTypeResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(biometricTypeService.getAllBiometricTypesByLanguageCode(langCode));
		return responseWrapper;
	}

	/**
	 * API to fetch Biometric type details based on code and language code
	 * 
	 * @param code     the code
	 * @param langCode the language code
	 * @return Biometric type
	 */
	@ResponseFilter
	@GetMapping("/{code}/{langcode}")
	public ResponseWrapper<BiometricTypeResponseDto> getBiometricTypeByCodeAndLangCode(
			@PathVariable("code") String code, @PathVariable("langcode") String langCode) {

		ResponseWrapper<BiometricTypeResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(biometricTypeService.getBiometricTypeByCodeAndLangCode(code, langCode));
		return responseWrapper;
	}

	/**
	 * API to insert Biometric type
	 * 
	 * @param biometricType is of type {@link BiometricType}
	 * 
	 * @return {@link CodeAndLanguageCodeID}
	 */
	@ResponseFilter
	@PostMapping
	public ResponseWrapper<CodeAndLanguageCodeID> createBiometricType(
			@Valid @RequestBody RequestWrapper<BiometricTypeDto> biometricType) {

		ResponseWrapper<CodeAndLanguageCodeID> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(biometricTypeService.createBiometricType(biometricType.getRequest()));
		return responseWrapper;
	}
}
