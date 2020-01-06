package io.mosip.preregistration.core.common.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;


@Data
public class CryptoManagerRequestDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1614969246905533759L;
	/**
	 * Application id of decrypting module
	 */

	private String applicationId;

	/**
	 * Data in BASE64 encoding to encrypt/decrypt
	 */
	private String data;

	/**
	 * Refrence Id
	 */

	private String referenceId;
	/**
	 * Timestamp
	 */

	private LocalDateTime timeStamp;

}
