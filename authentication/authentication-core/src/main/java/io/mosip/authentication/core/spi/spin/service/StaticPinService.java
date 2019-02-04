package io.mosip.authentication.core.spi.spin.service;

import io.mosip.authentication.core.dto.spinstore.StaticPinRequestDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

/**
 * 
 * @author Prem Kumar
 *
 */
public interface StaticPinService {
	
	boolean storeSpin(StaticPinRequestDTO staticPinRequestDTO, String uinValue) throws IdAuthenticationBusinessException;

}
