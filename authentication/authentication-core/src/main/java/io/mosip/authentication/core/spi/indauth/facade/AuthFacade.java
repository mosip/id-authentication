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
	 * Process the authorization type and authorization response is returned.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param isAuth         boolean i.e is auth type request.
	 * @return AuthResponseDTO the auth response DTO
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception.
	 */
	AuthResponseDTO authenticateIndividual(AuthRequestDTO authRequest, boolean request,String partnerId)
			throws IdAuthenticationBusinessException, IdAuthenticationDaoException;

}
