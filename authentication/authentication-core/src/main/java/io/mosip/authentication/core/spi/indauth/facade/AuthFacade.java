package io.mosip.authentication.core.spi.indauth.facade;

import org.springframework.stereotype.Service;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthResponseDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

/**
 * 
 * @author Arun Bose
 */

@Service
public interface AuthFacade {
	 
	AuthResponseDTO authenticateApplicant(AuthRequestDTO authRequest) throws IdAuthenticationBusinessException;

}
