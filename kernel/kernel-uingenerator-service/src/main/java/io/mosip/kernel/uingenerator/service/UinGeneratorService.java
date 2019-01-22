/**
 * 
 */
package io.mosip.kernel.uingenerator.service;

import io.mosip.kernel.uingenerator.dto.UinResponseDto;

/**
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
public interface UinGeneratorService {

	/**
	 * Gets a uin from database
	 * 
	 * @return UinResponseDto
	 */
	UinResponseDto getUin();

}