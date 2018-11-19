package io.mosip.kernel.keymanagerservice.service;

import java.time.LocalDateTime;
import java.util.Optional;

import io.mosip.kernel.keymanagerservice.dto.KeyResponseDto;

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
	public KeyResponseDto decryptSymmetricKey(String appId, LocalDateTime timeStamp, Optional<String> machineId,
			byte[] encryptedSymmetricKey);

	/**
	 * @param appId
	 * @param timeStamp
	 * @param machineId
	 * @return
	 */
	public KeyResponseDto getPublicKey(String appId, LocalDateTime timeStamp, Optional<String> machineId);
}
