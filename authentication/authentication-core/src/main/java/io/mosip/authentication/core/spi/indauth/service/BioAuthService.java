package io.mosip.authentication.core.spi.indauth.service;

/**
 * 
 * This interface is used to authenticate Individual based on Biometric
 * attributes.
 * 
 * @author Dinesh Karuppiah.T
 */

public interface BioAuthService extends AuthService {
	
	/**
	 * Method to validate Bio Auth details.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param idInfo the id info
	 * @return the auth status info
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 *//*

	AuthStatusInfo validateBioDetails(AuthRequestDTO authRequestDTO, Map<String, List<IdentityInfoDTO>> idInfo)
			throws IdAuthenticationBusinessException;*/

}