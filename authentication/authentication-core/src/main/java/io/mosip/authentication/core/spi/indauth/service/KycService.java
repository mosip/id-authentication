package io.mosip.authentication.core.spi.indauth.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.KycResponseDTO;

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
	public KycResponseDTO retrieveKycInfo(List<String> eKycTypeAttributes, Set<String> langCodes,
			Map<String, List<IdentityInfoDTO>> identityInfo) throws IdAuthenticationBusinessException;

}
