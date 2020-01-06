package io.mosip.authentication.core.spi.indauth.facade;

import org.springframework.stereotype.Service;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthResponseDTO;

/**
 * The Interface AuthFacade.
 *
 * @author Arun Bose
 */

@Service
public interface AuthFacade {

	/**
	 * Process the authorization type and authorization response is returned.
	 *
	 * @param authRequest the auth request
	 * @param request the request
	 * @param partnerId the partner id
	 * @return AuthResponseDTO the auth response DTO
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception.
	 * @throws IdAuthenticationDaoException the id authentication dao exception
	 */
	AuthResponseDTO authenticateIndividual(AuthRequestDTO authRequest, boolean request,String partnerId)
			throws IdAuthenticationBusinessException, IdAuthenticationDaoException;

}
