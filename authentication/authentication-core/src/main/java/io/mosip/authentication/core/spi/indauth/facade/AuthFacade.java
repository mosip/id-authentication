package io.mosip.authentication.core.spi.indauth.facade;

import org.springframework.stereotype.Service;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthResponseDTO;
import io.mosip.authentication.core.dto.indauth.KycAuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.KycAuthResponseDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;

/**
 * The Interface AuthFacade.
 *
 * @author Arun Bose
 */

@Service
public interface AuthFacade {

	/**
	 * Authenticate applicant.
	 *
	 * @param authRequest the auth request
	 * @return the auth response DTO
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	AuthResponseDTO authenticateApplicant(AuthRequestDTO authRequest, boolean request)
			throws IdAuthenticationBusinessException, IdAuthenticationDaoException;

	AuthResponseDTO authenticateTsp(AuthRequestDTO authRequestDTO);

	KycAuthResponseDTO processKycAuth(KycAuthRequestDTO kycAuthRequestDTO, AuthResponseDTO authResponseDTO)
			throws IdAuthenticationBusinessException;

}
