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
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
@Service
public interface CryptomanagerService {
 /**
 * @param cryptoRequestDto
 * @return
 */
public CryptomanagerResponseDto encrypt(
			@Valid CryptomanagerRequestDto cryptoRequestDto);

/**
 * @param cryptoRequestDto
 * @return
 */
public CryptomanagerResponseDto decrypt(
		@Valid CryptomanagerRequestDto cryptoRequestDto);

}
