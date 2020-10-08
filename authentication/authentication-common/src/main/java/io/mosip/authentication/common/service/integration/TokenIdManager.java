 package io.mosip.authentication.common.service.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.tokenidgenerator.dto.TokenIDResponseDto;
import io.mosip.kernel.tokenidgenerator.service.TokenIDGeneratorService;


/**
 * This Class will call an rest api which accepts uin, partnerId and will return
 * authTokenId.
 * 
 * @author Prem Kumar
 *
 */
@Component
public class TokenIdManager {

	@Autowired(required = false)
	TokenIDGeneratorService tokenIDGeneratorService;
	/**
	 * Token ID Manager Logger
	 */
	private static Logger logger = IdaLogger.getLogger(TokenIdManager.class);

	public String generateTokenId(String uin, String partnerId) throws IdAuthenticationBusinessException {
			
		try {
			TokenIDResponseDto response = tokenIDGeneratorService.generateTokenID(uin, partnerId);
			return response.getTokenID();
		} catch (Exception e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getName(),e.getLocalizedMessage(),e.getMessage());
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.SERVER_ERROR, e);
		}
	}
}
