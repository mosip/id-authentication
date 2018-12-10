package io.mosip.kernel.keymanagerservice.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for Symmetric Key
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SymmetricKeyRequestDto {

	/**
	 * The string applicationID
	 */
	private String applicationId;

	/**
	 * The field for timestamp
	 */
	private LocalDateTime timeStamp;

	/**
	 * The string reference id
	 */
	private String referenceId;

	/**
	 * The string encryptedSymmetricKey
	 */
	private String encryptedSymmetricKey;

}
