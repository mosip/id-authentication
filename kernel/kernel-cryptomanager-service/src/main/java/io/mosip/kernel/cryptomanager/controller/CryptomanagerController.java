/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.cryptomanager.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.cryptomanager.dto.CryptomanagerRequestDto;
import io.mosip.kernel.cryptomanager.dto.CryptomanagerResponseDto;
import io.mosip.kernel.cryptomanager.service.CryptomanagerService;

/** Rest Controller for Cryptographic Service
 * 
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
@CrossOrigin
@RestController
@RequestMapping("/v1.0")
public class CryptomanagerController {

	/**
	 * 
	 */
	@Autowired
	CryptomanagerService cryptoService;

	
	/**
	 * @param cryptoRequestDto
	 * @return
	 */
	@PostMapping(value = "/encrypt")
	public CryptomanagerResponseDto encrypt(@RequestBody @Valid CryptomanagerRequestDto cryptoRequestDto) {
		return cryptoService.encrypt(cryptoRequestDto);
	}

	
	/**
	 * @param cryptoRequestDto
	 * @return
	 */
	@PostMapping(value = "/decrypt")
	public CryptomanagerResponseDto decrypt(@RequestBody @Valid CryptomanagerRequestDto cryptoRequestDto) {
		return cryptoService.decrypt(cryptoRequestDto);
	}
}
