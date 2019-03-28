package io.mosip.registration.service;

import io.mosip.registration.dto.ResponseDTO;

/**
 * 
 * @author Brahmananda Reddy
 * @since 1.0.0
 *
 */
public interface PolicySyncService {
	/**
	 * it gets the key from server
	 * 
	 * @return ResponseDTO
	 */
	ResponseDTO fetchPolicy();

	/**
	 * it checks either key is valid or not
	 * 
	 * @return ResponseDTO
	 */
	ResponseDTO checkKeyValidation();
}
