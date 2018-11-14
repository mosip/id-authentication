package io.mosip.kernel.cryptography.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.cryptography.dto.CryptographyRequestDto;
import io.mosip.kernel.cryptography.dto.CryptographyResponseDto;
import io.mosip.kernel.cryptography.service.CryptographyService;

@RestController
public class CryptoraphyController {

	@Autowired
	CryptographyService cryptographyService; 
	
	@PostMapping(value="/encrypt")
	public CryptographyResponseDto encrypt(@RequestBody @Valid CryptographyRequestDto cryptographyRequestDto) {
		return cryptographyService.encrypt(cryptographyRequestDto);
	}
	

	@PostMapping(value="/decrypt")
	public CryptographyResponseDto decrypt(@RequestBody @Valid CryptographyRequestDto cryptographyRequestDto) {
		return cryptographyService.decrypt(cryptographyRequestDto);
	}
}
