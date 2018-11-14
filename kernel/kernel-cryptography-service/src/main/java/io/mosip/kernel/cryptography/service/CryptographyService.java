package io.mosip.kernel.cryptography.service;

import org.springframework.stereotype.Service;

import io.mosip.kernel.cryptography.dto.CryptographyRequestDto;
import io.mosip.kernel.cryptography.dto.CryptographyResponseDto;

@Service
public interface CryptographyService {

	public CryptographyResponseDto encrypt(CryptographyRequestDto cryptographyRequestDto);

	public CryptographyResponseDto decrypt(CryptographyRequestDto cryptographyRequestDto);

}
