package io.mosip.kernel.keymanagerservice.service;

import java.time.LocalDateTime;
import java.util.Optional;

import io.mosip.kernel.keymanagerservice.dto.PublicKeyResponse;
import io.mosip.kernel.keymanagerservice.dto.SymmetricKeyRequestDto;
import io.mosip.kernel.keymanagerservice.dto.SymmetricKeyResponseDto;

/**
 * This interface provides the methods which can be used for Key management
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
public interface KeymanagerService {

	/**
	 * @param appId
	 * @param timeStamp
	 * @param machineId
	 * @param encryptedSymmetricKey
	 * @return
	 */
	public SymmetricKeyResponseDto decryptSymmetricKey(SymmetricKeyRequestDto symmetricKeyRequestDto);

	/**
	 * @param applicationId
	 * @param timeStamp
	 * @param referenceId
	 * @return
	 */
	public PublicKeyResponse<String> getPublicKey(String applicationId, LocalDateTime timeStamp,
			Optional<String> referenceId);

}
