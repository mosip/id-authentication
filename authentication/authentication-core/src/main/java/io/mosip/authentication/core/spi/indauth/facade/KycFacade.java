package io.mosip.authentication.core.spi.indauth.facade;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.KycAuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.KycAuthResponseDTO;

/**
 * This class used to integrate with kyc service
 * 
 * @author Sanjay Murali
 */
public interface KycFacade {
	
	/**
	 * Process the KycAuthRequestDTO to integrate with KycService.
	 *
	 * @param kycAuthRequestDTO is DTO of KycAuthRequestDTO
	 * @param authResponseDTO   the auth response DTO
	 * @return the kyc auth response DTO
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	KycAuthResponseDTO processKycAuth(KycAuthRequestDTO kycAuthRequestDTO, AuthResponseDTO authResponseDTO,
			String partnerId) throws IdAuthenticationBusinessException;
	
	/**
	 * Authenticate individual.
	 *
	 * @param authRequest the auth request
	 * @param request the request
	 * @param partnerId the partner id
	 * @return the auth response DTO
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 * @throws IdAuthenticationDaoException the id authentication dao exception
	 */
	AuthResponseDTO authenticateIndividual(AuthRequestDTO authRequest, boolean request, String partnerId)
			throws IdAuthenticationBusinessException, IdAuthenticationDaoException;

}
