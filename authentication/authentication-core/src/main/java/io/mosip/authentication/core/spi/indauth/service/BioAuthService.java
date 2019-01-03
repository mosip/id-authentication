package io.mosip.authentication.core.spi.indauth.service;

import java.util.List;
import java.util.Map;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthStatusInfo;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

/**
 * 
 * This interface is used to authenticate Individual based on Biometric
 * attributes.
 * 
 * @author Dinesh Karuppiah.T
 */

public interface BioAuthService {
	/**
	 * Method to validate Bio Auth details
	 * 
	 * @param authRequestDTO
	 * @param idInfo
	 * @return
	 * @throws IdAuthenticationBusinessException
	 */

	AuthStatusInfo validateBioDetails(AuthRequestDTO authRequestDTO, Map<String, List<IdentityInfoDTO>> idInfo)
			throws IdAuthenticationBusinessException;

}