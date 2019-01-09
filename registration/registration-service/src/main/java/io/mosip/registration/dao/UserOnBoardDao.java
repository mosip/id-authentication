package io.mosip.registration.dao;

import io.mosip.registration.dto.biometric.BiometricDTO;

/**
 * @author Sreekar Chukka
 *
 * @since 1.0.0
 */
public interface UserOnBoardDao {

	/**
	 * Insert.
	 *
	 * @param biometricDTO the biometric DTO
	 * @return the string
	 */
	String insert(BiometricDTO biometricDTO);
	
}
