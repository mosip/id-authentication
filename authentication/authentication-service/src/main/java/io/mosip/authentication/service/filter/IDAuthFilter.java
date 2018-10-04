package io.mosip.authentication.service.filter;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthResponseDTO;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;

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
