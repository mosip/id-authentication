package io.mosip.authentication.core.spi.keymanager.service;

import java.util.Optional;

import io.mosip.authentication.core.indauth.dto.PublicKeyResponseDto;

public interface KeymanagerService {	
	/**
	 * Function to get public key
	 * 
	 * @param applicationId applicationId
	 * @param timeStamp     timeStamp
	 * @param referenceId   referenceId
	 * @return {@link PublicKeyResponse} instance
	 */
	public PublicKeyResponseDto<String> getPublicKey(String applicationId, String timeStamp, Optional<String> referenceId);

	

	public PublicKeyResponseDto<String> getSignPublicKey(String applicationId, String timeStamp,
			Optional<String> referenceId);

	
}
