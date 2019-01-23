package io.mosip.authentication.core.spi.indauth.service;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthStatusInfo;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

/**
 * The Interface PinAuthService.
 * 
 * @author Sanjay Murali
 */
public interface PinAuthService {

	AuthStatusInfo validatePin(AuthRequestDTO authRequestDTO, String uin) throws IdAuthenticationBusinessException;
}
