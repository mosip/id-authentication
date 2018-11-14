package io.mosip.kernel.keymanager.service;

import java.time.LocalDateTime;
import java.util.Optional;

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
	public byte[] decryptSymmetricKey(String appId, LocalDateTime timeStamp, Optional<String> machineId,
			byte[] encryptedSymmetricKey);

	/**
	 * @param appId
	 * @param timeStamp
	 * @param machineId
	 * @return
	 */
	public byte[] getPublicKey(String appId, LocalDateTime timeStamp, Optional<String> machineId);
}
