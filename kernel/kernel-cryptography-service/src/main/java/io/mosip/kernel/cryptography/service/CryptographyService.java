package io.mosip.kernel.cryptography.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import io.mosip.kernel.cryptography.dto.CryptographyResponseDto;

@Service
public interface CryptographyService {

	public CryptographyResponseDto encrypt(String applicationId, byte[] data,
			LocalDateTime timeStamp, Optional<String> machineId);

	public CryptographyResponseDto decrypt(String applicationId, byte[] data,
			LocalDateTime timeStamp, Optional<String> machineId);

}
