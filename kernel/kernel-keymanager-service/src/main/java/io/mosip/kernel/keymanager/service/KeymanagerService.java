package io.mosip.kernel.keymanager.service;

import io.mosip.kernel.keymanager.dto.KeymanagerResponseDto;

/**
 * This interface provides the methods which can be used for Key management
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
public interface KeymanagerService {

	public KeymanagerResponseDto getPublicKey();
}
