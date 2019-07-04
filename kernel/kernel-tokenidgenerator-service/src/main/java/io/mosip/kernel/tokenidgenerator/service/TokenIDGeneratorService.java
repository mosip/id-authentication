/**
 * 
 */
package io.mosip.kernel.tokenidgenerator.service;

import io.mosip.kernel.tokenidgenerator.dto.TokenIDResponseDto;


/**
 * @author Urvil Joshi
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
public interface TokenIDGeneratorService {

	/**
	 * @param uin
	 * @param partnerCode
	 * @return
	 */
	TokenIDResponseDto generateTokenID(String uin, String partnerCode);

}