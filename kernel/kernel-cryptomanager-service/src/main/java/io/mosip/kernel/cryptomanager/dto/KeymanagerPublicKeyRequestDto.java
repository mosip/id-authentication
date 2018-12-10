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
 * Key Manager get-public-key request
 * 
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeymanagerPublicKeyRequestDto {
	/**
	 * Application Id
	 */
	private String applicationId;
	/**
	 * Reference Id
	 */
	private String referenceId;
	/**
	 * TimeStamp
	 */
	private LocalDateTime timeStamp;

}
