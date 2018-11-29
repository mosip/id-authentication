package io.mosip.authentication.core.spi.indauth.service;

import java.util.List;
import java.util.Map;

import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.KycInfo;
import io.mosip.authentication.core.dto.indauth.KycType;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

/**
 * This interface is used to retrieve Kyc information of individual
 * 
 * @author Sanjay Murali
 */
@FunctionalInterface
public interface KycService {
	
	public KycInfo retrieveKycInfo(String uin, KycType eKycType, boolean ePrintReq, boolean isSecLangInfoRequired,
			Map<String, List<IdentityInfoDTO>> identityInfo) throws IdAuthenticationBusinessException;

	
}
