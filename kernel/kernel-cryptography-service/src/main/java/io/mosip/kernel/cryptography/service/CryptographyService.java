/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.cryptography.service;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import io.mosip.kernel.cryptography.dto.CryptographyRequestDto;
import io.mosip.kernel.cryptography.dto.CryptographyResponseDto;

/**
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
@Service
public interface CryptographyService {
 /**
 * @param cryptographyRequestDto
 * @return
 */
public CryptographyResponseDto encrypt(
			@Valid CryptographyRequestDto cryptographyRequestDto);

/**
 * @param cryptographyRequestDto
 * @return
 */
public CryptographyResponseDto decrypt(
		@Valid CryptographyRequestDto cryptographyRequestDto);

}
