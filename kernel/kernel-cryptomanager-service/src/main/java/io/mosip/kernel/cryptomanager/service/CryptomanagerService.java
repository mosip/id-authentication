/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.cryptomanager.service;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import io.mosip.kernel.cryptomanager.dto.CryptomanagerRequestDto;
import io.mosip.kernel.cryptomanager.dto.CryptomanagerResponseDto;

/**
 * This interface provides the methods which can be used for Encryption and
 * Decryption
 * 
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
@Service
public interface CryptomanagerService {
	/**
	 * Encrypt the data requested with metadata
	 * 
	 * @param cryptoRequestDto
	 *            {@link CryptomanagerRequestDto} instance
	 * @return encrypted data
	 */
	public CryptomanagerResponseDto encrypt(
			@Valid CryptomanagerRequestDto cryptoRequestDto);

	/**
	 * Decrypt data requested with metadata
	 * 
	 * @param cryptoRequestDto
	 *            {@link CryptomanagerRequestDto} instance
	 * @return decrypted data
	 */
	public CryptomanagerResponseDto decrypt(
			@Valid CryptomanagerRequestDto cryptoRequestDto);

}
