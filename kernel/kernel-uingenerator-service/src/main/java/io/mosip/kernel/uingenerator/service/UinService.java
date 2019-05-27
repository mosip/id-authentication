/**
 * 
 */
package io.mosip.kernel.uingenerator.service;

import io.mosip.kernel.uingenerator.dto.UinResponseDto;
import io.mosip.kernel.uingenerator.dto.UinStatusUpdateReponseDto;
import io.mosip.kernel.uingenerator.entity.UinEntity;

/**
 * @author Dharmesh Khandelwal
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
public interface UinService {

	/**
	 * Gets a uin from database
	 * 
	 * @return UinResponseDto
	 */
	UinResponseDto getUin();

	/**
	 * Upodate the status of the Uin from ISSUED to ASSIGNED
	 * 
	 * @param uin pass uin object as param
	 * 
	 * @return UinStatusUpdateReponseDto
	 */
	UinStatusUpdateReponseDto updateUinStatus(UinEntity uin);

}