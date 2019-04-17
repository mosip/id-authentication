/**
 * 
 */
package io.mosip.authentication.kyc.service.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthResponseDTO;
import io.mosip.authentication.core.spi.indauth.facade.AuthFacade;

/**
 * @author M1047697
 *
 */
@Component
public class KycFacade {

	@Autowired
	private AuthFacade authFacade;

	public AuthResponseDTO authenticateIndividual(AuthRequestDTO authRequest, boolean request, String partnerId)
			throws IdAuthenticationBusinessException, IdAuthenticationDaoException {
		return authFacade.authenticateIndividual(authRequest, request, partnerId);

	}

}
