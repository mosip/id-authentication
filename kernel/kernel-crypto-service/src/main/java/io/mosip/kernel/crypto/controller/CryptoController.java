/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.crypto.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.crypto.dto.CryptoRequestDto;
import io.mosip.kernel.crypto.dto.CryptoResponseDto;
import io.mosip.kernel.crypto.service.CryptoService;

/** Rest Controller for Cryptographic Service
 * 
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
@CrossOrigin
@RestController
public class CryptoController {

	/**
	 * 
	 */
	@Autowired
	CryptoService cryptoService;

	
	/**
	 * @param cryptoRequestDto
	 * @return
	 */
	@PostMapping(value = "/encrypt")
	public CryptoResponseDto encrypt(@RequestBody @Valid CryptoRequestDto cryptoRequestDto) {
		return cryptoService.encrypt(cryptoRequestDto);
	}

	
	/**
	 * @param cryptoRequestDto
	 * @return
	 */
	@PostMapping(value = "/decrypt")
	public CryptoResponseDto decrypt(@RequestBody @Valid CryptoRequestDto cryptoRequestDto) {
		System.out.println(cryptoRequestDto.getTimeStamp());
		return cryptoService.decrypt(cryptoRequestDto);
	}
}
