package io.mosip.authentication.core.spi.indauth.service;

import java.util.List;
import java.util.Map;

import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.KycResponseDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

/**
 * This interface is used to retrieve Kyc information of individual
 * 
 * @author Sanjay Murali
 */
@FunctionalInterface
public interface KycService {

	/**
	 * Method used to retrieve the KYC information as per the policy
	 *
	 * @param uin the individualId of the Resident
	 * @param eKycTypeAttributes the policy described for partner
	 * @param secLangCode - secondary language code to be sent if details need
	 * @param identityInfo the information contains personal information of the resident
	 * @return the kycResponseDTO consists of the KYC Information of the resident
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	public KycResponseDTO retrieveKycInfo(String uin, List<String> eKycTypeAttributes, String secLangCode,
			Map<String, List<IdentityInfoDTO>> identityInfo) throws IdAuthenticationBusinessException;

}
