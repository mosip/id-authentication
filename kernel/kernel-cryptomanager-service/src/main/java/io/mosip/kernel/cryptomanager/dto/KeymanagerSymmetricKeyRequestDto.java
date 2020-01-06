/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.cryptomanager.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Key-Manager-Service decrypt-symmetric-key request model
 * 
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeymanagerSymmetricKeyRequestDto {

	/**
	 * Application Id
	 */
	private String applicationId;

	/**
	 * Timestamp as metadata
	 */
	private LocalDateTime timeStamp;

	/**
	 * Refrence Id
	 */
	private String referenceId;

	/**
	 * Encrypted Symmetric key
	 */
	private String encryptedSymmetricKey;
}
