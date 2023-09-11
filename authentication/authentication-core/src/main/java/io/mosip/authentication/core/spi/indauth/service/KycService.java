package io.mosip.authentication.core.spi.indauth.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.KycExchangeRequestDTO;
import io.mosip.authentication.core.indauth.dto.EKycResponseDTO;

/**
 * This interface is used to retrieve Kyc information of individual
 * 
 * @author Sanjay Murali
 */
public interface KycService {

	/**
	 * Method used to retrieve the KYC information as per the policy
	 *
	 * @param uin                the individualId of the Resident
	 * @param eKycTypeAttributes the policy described for partner
	 * @param secLangCode        - secondary language code to be sent if details
	 *                           need
	 * @param identityInfo       the information contains personal information of
	 *                           the resident
	 * @return the kycResponseDTO consists of the KYC Information of the resident
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	public EKycResponseDTO retrieveKycInfo(List<String> eKycTypeAttributes, Set<String> langCodes,
			Map<String, List<IdentityInfoDTO>> identityInfo) throws IdAuthenticationBusinessException;


	/**
	 * Method used to generate the KYC Token after successful authentication and store the kyc token details in DB. 
	 *
	 * @param idHash        	 	Id Hash of the inputted Id
	 * @param authToken   			Partner specific User Token
	 * @param oidcClientId   		OIDC Client Id
	 * @param requestTime   		Auth Request Time
	 * @param tokenGenerationTime   Token Generation Time
	 * @param reqTransactionId   	Request Transaction Id
	 * 
	 * @return the String generated Kyc Token
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	String generateAndSaveKycToken(String idHash, String authToken, String oidcClientId, String requestTime, 
				String tokenGenerationTime, String reqTransactionId) throws IdAuthenticationBusinessException;

	
	/**
	 * Method used to validate the input KYC Token is expired or not. 
	 *
	 * @param tokenIssuedDateTime			Token Issued DataTime
	 * @param kycToken        	 	The KYC Token
	 * @return void
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	boolean isKycTokenExpire(LocalDateTime tokenIssuedDateTime, String kycToken) throws IdAuthenticationBusinessException;


	/**
	 * Method to build kyc exchange response to return user claims. 
	 *
	 * @param subject 							Partner Specific token as subject for user claims
	 * @param idInfo							List of Identity Info of the user.
	 * @param consentedAttributes        	 	Consented Attributes.
	 * @param locales							selected locales.
	 * @param idVid						        individual Id
	 * @return String
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	String buildKycExchangeResponse(String subject, Map<String, List<IdentityInfoDTO>> idInfo, 
				List<String> consentedAttributes, List<String> locales, String idVid, KycExchangeRequestDTO kycExchangeRequestDTO) throws IdAuthenticationBusinessException;
}
