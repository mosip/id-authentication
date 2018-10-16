package io.mosip.authentication.service.filter;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthResponseDTO;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;

/**
 * Auth Filter for OTP Auth service.
 *
 * @author Loganathan Sekar
 */
public class IDAuthFilter extends TSPAuthFilter<AuthRequestDTO, AuthResponseDTO> {

	/* (non-Javadoc)
	 * @see io.mosip.authentication.service.filter.BaseAuthFilter#getRequestDTOClass()
	 */
	@Override
	protected Class<AuthRequestDTO> getRequestDTOClass() {
		return AuthRequestDTO.class;
	}


	/* (non-Javadoc)
	 * @see io.mosip.authentication.service.filter.BaseAuthFilter#getAuthInfo(java.lang.Object)
	 */
	@Override
	protected TSPInfo getAuthInfo(AuthRequestDTO requestDTO) throws IdAuthenticationAppException {
		return null;
	}

}
