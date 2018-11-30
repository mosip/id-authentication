package io.mosip.kernel.masterdata.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.BiometricAttributeDto;
import io.mosip.kernel.masterdata.dto.BiometricAttributeRequestDto;
import io.mosip.kernel.masterdata.dto.getresponse.BiometricAttributeResponseDto;
import io.mosip.kernel.masterdata.dto.BioTypeCodeAndLangCodeAndAttributeCode;
import io.mosip.kernel.masterdata.service.BiometricAttributeService;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * @author Uday Kumar
 * @since 1.0.0
 *
 */
@RestController
public class BiometricAttributeController {
	/**
	 * biometric attribute controller with api to get list of biometric attributes
	 * best on biometric code type and language code.
	 * 
	 */
	@Autowired
	BiometricAttributeService biometricAttributeService;

	@ApiOperation(value = "Fetch all the biometric attributes avialbale for specific BiometricType")
	@GetMapping("/getbiometricattributesbyauthtype/{langcode}/{biometrictypecode}")
	public BiometricAttributeResponseDto getBiometricAttributesByBiometricType(
			@PathVariable("langcode") String langCode, @PathVariable("biometrictypecode") String biometricTypeCode) {
		List<BiometricAttributeDto> biomentricAttributes = biometricAttributeService
				.getBiometricAttribute(biometricTypeCode, langCode);
		return new BiometricAttributeResponseDto(biomentricAttributes);
	}

	/**
	 * Save device biometric attribute details in database table
	 * 
	 * @param biometricAttribute
	 *            input from user Biometric Attribute DTO
	 * @return {@link BioTypeCodeAndLangCodeAndAttributeCode}
	 */
	@PostMapping("/biometricattributes")
	public ResponseEntity<BioTypeCodeAndLangCodeAndAttributeCode> createBiometricAttribute(
			@RequestBody BiometricAttributeRequestDto biometricAttribute) {
		return new ResponseEntity<>(biometricAttributeService.createBiometricAttribute(biometricAttribute),
				HttpStatus.OK);
	}

}
