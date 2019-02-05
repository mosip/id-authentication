package io.mosip.authentication.core.spi.spin.facade;

import io.mosip.authentication.core.dto.spinstore.StaticPinRequestDTO;
import io.mosip.authentication.core.dto.spinstore.StaticPinResponseDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

/**
 * 
 * @author Prem Kumar
 *
 */
public interface StaticPinFacade {
	
	StaticPinResponseDTO storeSpin(StaticPinRequestDTO staticPinRequestDTO) throws IdAuthenticationBusinessException;
}
