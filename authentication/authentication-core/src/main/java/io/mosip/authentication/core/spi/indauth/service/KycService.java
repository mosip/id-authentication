package io.mosip.authentication.core.spi.indauth.service;

import io.mosip.authentication.core.dto.indauth.KycInfo;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

/**
 * This interface is used to retrieve Kyc information of individual
 * 
 * @author Sanjay Murali
 */
@FunctionalInterface
public interface KycService {
	
	public KycInfo retrieveKycInfo(String uin, String eKycType, boolean ePrintReq, boolean isSecLangInfoRequired) throws IdAuthenticationBusinessException;
	
}
