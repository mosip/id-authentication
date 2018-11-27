package io.mosip.kernel.keymanagerservice.service;

import io.mosip.kernel.keymanagerservice.dto.KeyResponseDto;
import io.mosip.kernel.keymanagerservice.dto.PublicKeyRequestDto;
import io.mosip.kernel.keymanagerservice.dto.SymmetricKeyRequestDto;

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
	public KeyResponseDto decryptSymmetricKey(SymmetricKeyRequestDto symmetricKeyRequestDto);

	/**
	 * @param appId
	 * @param timeStamp
	 * @param string
	 * @return
	 */
	public KeyResponseDto getPublicKey(PublicKeyRequestDto publicKeyRequestDto);
}
