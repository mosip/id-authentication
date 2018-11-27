/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.crypto.service;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import io.mosip.kernel.crypto.dto.CryptoRequestDto;
import io.mosip.kernel.crypto.dto.CryptoResponseDto;

/**
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
@Service
public interface CryptoService {
 /**
 * @param cryptoRequestDto
 * @return
 */
public CryptoResponseDto encrypt(
			@Valid CryptoRequestDto cryptoRequestDto);

/**
 * @param cryptoRequestDto
 * @return
 */
public CryptoResponseDto decrypt(
		@Valid CryptoRequestDto cryptoRequestDto);

}
