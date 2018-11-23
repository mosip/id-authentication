/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.cryptography.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.cryptography.dto.CryptographyRequestDto;
import io.mosip.kernel.cryptography.dto.CryptographyResponseDto;
import io.mosip.kernel.cryptography.service.CryptographyService;

/** Rest Controller for Cryptographic Service
 * 
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
@RestController
public class CryptoraphyController {

	/**
	 * 
	 */
	@Autowired
	CryptographyService cryptographyService;

	
	/**
	 * @param cryptographyRequestDto
	 * @return
	 */
	@PostMapping(value = "/encrypt")
	public CryptographyResponseDto encrypt(@RequestBody @Valid CryptographyRequestDto cryptographyRequestDto) {
		return cryptographyService.encrypt(cryptographyRequestDto);
	}

	
	/**
	 * @param cryptographyRequestDto
	 * @return
	 */
	@PostMapping(value = "/decrypt")
	public CryptographyResponseDto decrypt(@RequestBody @Valid CryptographyRequestDto cryptographyRequestDto) {
		return cryptographyService.decrypt(cryptographyRequestDto);
	}
}
