package io.mosip.authentication.core.spi.indauth.service;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthStatusInfo;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

/**
 * 
 * This interface is used to authenticate Individual based on Demo attributes.
 * 
 * @author Gurpreet Bagga
 */
@FunctionalInterface
public interface DemoAuthService {
	/**
	 * Gets the demo status.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @return the demo status
	 */
	AuthStatusInfo getDemoStatus(AuthRequestDTO authRequestDTO, String refId) throws IdAuthenticationBusinessException;
}