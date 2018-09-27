package org.mosip.auth.core.spi.indauth.facade;

import org.mosip.auth.core.dto.indauth.AuthRequestDTO;
import org.mosip.auth.core.dto.indauth.AuthResponseDTO;
import org.mosip.auth.core.exception.IdAuthenticationBusinessException;
import org.springframework.stereotype.Service;

/**
 * 
 * @author Arun Bose
 */

@Service
public interface AuthFacade {
	 
	AuthResponseDTO authenticateApplicant(AuthRequestDTO authRequest) throws IdAuthenticationBusinessException;

}
