package org.mosip.auth.service.filter;

import org.mosip.auth.core.dto.indauth.AuthRequestDTO;
import org.mosip.auth.core.dto.indauth.AuthResponseDTO;
import org.mosip.auth.core.exception.IdAuthenticationAppException;

/**
 * Auth Filter for OTP Auth service
 *
 * @author Loganathan Sekar
 */
public class IDAuthFilter extends TSPAuthFilter<AuthRequestDTO, AuthResponseDTO> {

	@Override
	protected AuthResponseDTO createResponseDTO(IdAuthenticationAppException e) {
		//FIXME call the Error Response builder
		//return IDAuthenticationExceptionHandler.buildOtpAuthExceptionResponse(e);
		return null;
	}

	@Override
	protected Class<AuthRequestDTO> getRequestDTOClass() {
		return AuthRequestDTO.class;
	}


	@Override
	protected TSPInfo getAuthInfo(AuthRequestDTO requestDTO) throws IdAuthenticationAppException {
		// TODO Auto-generated method stub
		return null;
	}

}
