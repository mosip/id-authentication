package io.mosip.kernel.keymanagerservice.service;

import java.util.List;
import java.util.Optional;

import io.mosip.kernel.keymanagerservice.dto.EncryptDataRequestDto;
import io.mosip.kernel.keymanagerservice.dto.EncryptDataResponseDto;
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
	 * Function to decrypt symmetric key
	 * 
	 * @param symmetricKeyRequestDto symmetricKeyRequestDto
	 * @return {@link SymmetricKeyResponseDto} instance
	 */
	public SymmetricKeyResponseDto decryptSymmetricKey(SymmetricKeyRequestDto symmetricKeyRequestDto);

	/**
	 * Function to get public key
	 * 
	 * @param applicationId applicationId
	 * @param timeStamp     timeStamp
	 * @param referenceId   referenceId
	 * @return {@link PublicKeyResponse} instance
	 */
	public PublicKeyResponse<String> getPublicKey(String applicationId, String timeStamp, Optional<String> referenceId);

	// TODO: To be removed added for debugging
	List<String> getAllAlias();

	
	
	/**
	 * encrypts the data with private key.
	 *
	 * @param encryptDataRequestDto the encrypt data request dto
	 * @return {@link EncryptDataResponseDto} encrypted data
	 */
	public EncryptDataResponseDto encrypt(EncryptDataRequestDto encryptDataRequestDto);

}
