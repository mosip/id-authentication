package io.mosip.kernel.keymanagerservice.service;

import java.util.Optional;

import io.mosip.kernel.keymanagerservice.dto.PublicKeyResponse;
import io.mosip.kernel.keymanagerservice.dto.SignatureRequestDto;
import io.mosip.kernel.keymanagerservice.dto.SignatureResponseDto;
import io.mosip.kernel.keymanagerservice.dto.SymmetricKeyRequestDto;
import io.mosip.kernel.keymanagerservice.dto.SymmetricKeyResponseDto;

/**
 * This interface provides the methods which can be used for Key management
 * 
 * @author Dharmesh Khandelwal
 * @author Urvil Joshi
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


	public SignatureResponseDto sign(SignatureRequestDto signatureRequestDto);

	public PublicKeyResponse<String> getSignPublicKey(String applicationId, String timeStamp, Optional<String> referenceId);
}
