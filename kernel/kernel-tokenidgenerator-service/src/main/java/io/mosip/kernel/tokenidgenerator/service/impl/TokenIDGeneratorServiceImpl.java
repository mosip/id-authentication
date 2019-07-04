/**
 * 
 */
package io.mosip.kernel.tokenidgenerator.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.idgenerator.exception.TokenIdGeneratorException;
import io.mosip.kernel.tokenidgenerator.constant.TokenIDGeneratorErrorCode;
import io.mosip.kernel.tokenidgenerator.dto.TokenIDResponseDto;
import io.mosip.kernel.tokenidgenerator.generator.TokenIDGenerator;
import io.mosip.kernel.tokenidgenerator.service.TokenIDGeneratorService;

/**
 * @author Urvil Joshi
 * @author Ritesh Sinha
 * @since 1.0.0
 */
@Component
public class TokenIDGeneratorServiceImpl implements TokenIDGeneratorService {

	@Autowired
	private TokenIDGenerator tokenIDGenerator;

	@Override
	public TokenIDResponseDto generateTokenID(String uin, String partnerCode) {
		if (uin.isEmpty() || partnerCode.isEmpty()) {
			throw new TokenIdGeneratorException(
					TokenIDGeneratorErrorCode.EMPTY_UIN_OR_PARTNERCODE_EXCEPTION.getErrorCode(),
					TokenIDGeneratorErrorCode.EMPTY_UIN_OR_PARTNERCODE_EXCEPTION.getErrorMessage());
		}
		TokenIDResponseDto tokenIDResponseDto = new TokenIDResponseDto();
		tokenIDResponseDto.setTokenID(tokenIDGenerator.generateTokenID(uin, partnerCode));
		return tokenIDResponseDto;
	}
}
