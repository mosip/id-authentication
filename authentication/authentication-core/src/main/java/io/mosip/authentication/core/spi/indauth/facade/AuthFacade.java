package io.mosip.authentication.core.spi.indauth.facade;

import org.springframework.stereotype.Service;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthResponseDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

/**
 * The Interface AuthFacade.
 *
 * @author Arun Bose
 */

@Service
@FunctionalInterface
public interface AuthFacade {
	 
	/**
	 * Authenticate applicant.
	 *
	 * @param authRequest the auth request
	 * @return the auth response DTO
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	AuthResponseDTO authenticateApplicant(AuthRequestDTO authRequest) throws IdAuthenticationBusinessException;

}
