 package io.mosip.authentication.common.service.integration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.RestHelper;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.dto.RestRequestDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * This Class will call an rest api which accepts uin, partnerId and will return
 * StaticTokenId.
 * 
 * @author Prem Kumar
 *
 */
@Component
public class TokenIdManager {

	/**
	 * The Rest request factory
	 */
	@Autowired
	private RestRequestFactory restFactory;

	/**
	 * The Rest Helper
	 */
	@Autowired
	private RestHelper restHelper;

	/**
	 * Token ID Manager Logger
	 */
	private static Logger logger = IdaLogger.getLogger(TokenIdManager.class);

	@SuppressWarnings("unchecked")
	public String generateTokenId(String uin, String partnerId) throws IdAuthenticationBusinessException {
		try {
			Map<String, String> params = new HashMap<>();
			params.put("uin", uin);

			params.put("partnercode", partnerId);
			RestRequestDTO buildRequest = restFactory.buildRequest(RestServicesConstants.TOKEN_ID_GENERATOR, null,
					Map.class);
			buildRequest.setPathVariables(params);
			Map<String, Object> response = restHelper.requestSync(buildRequest);
			Map<String, String> fetchResponse;
			if (response.get("response") instanceof Map) {
				fetchResponse = (Map<String, String>) response.get("response");
			} else {
				fetchResponse = Collections.emptyMap();
			}
			String tokenId = fetchResponse.get("tokenID");
			return tokenId;
		} catch (IDDataValidationException | RestServiceException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getName(), e.getErrorCode(), e.getErrorText());
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.SERVER_ERROR, e);
		}
	}
}
