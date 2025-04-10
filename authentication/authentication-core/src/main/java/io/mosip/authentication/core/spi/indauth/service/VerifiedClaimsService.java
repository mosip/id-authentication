package io.mosip.authentication.core.spi.indauth.service;

import java.util.List;
import java.util.Map;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.KycExchangeRequestDTOV2;

/**
 * This interface is used to retrieve Verified Claims information of individual
 * 
 * @author Mahammed Taheer
 */
public interface VerifiedClaimsService {

	/**
	 * Method to build kyc auth version 2 verified claims meta data. 
	 *
	 * @param verifiedClaimsData				Verified Claims data of the identity
	 * @param oidcClientId						OIDC Client Id.
	 * @return String
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	String buildVerifiedClaimsMetadata(String verifiedClaimsData, String oidcClientId) throws IdAuthenticationBusinessException;

	/**
	 * Method to build kyc exchange version 2 verified claims data. 
	 *
	 * @param subject 							Partner Specific token as subject for user claims
	 * @param idInfo							List of Identity Info of the user.
	 * @param consentedAttributes        	 	Consented Attributes.
	 * @param locales							selected locales.
	 * @param idVid						        individual Id
	 * @param kycExchangeRequestDTOV2 			KycExchangeRequestDTOV2 object.
	 * @return String
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	String buildExchangeVerifiedClaimsData(String subject, Map<String, List<IdentityInfoDTO>> idInfo, 
				List<String> unverifiedConsentClaims, List<String> verifiedConsentClaims, 
				List<String> locales, String idVid, 
				KycExchangeRequestDTOV2 kycExchangeRequestDTOV2 ) throws IdAuthenticationBusinessException;
}
