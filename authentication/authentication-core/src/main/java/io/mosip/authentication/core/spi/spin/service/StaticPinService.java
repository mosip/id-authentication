package io.mosip.authentication.core.spi.spin.service;

import io.mosip.authentication.core.dto.spinstore.StaticPinRequestDTO;
import io.mosip.authentication.core.dto.spinstore.StaticPinResponseDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

/**
 * 
 * @author Prem Kumar
 *
 */
public interface StaticPinService {
	
	/**
	 * Store spin.
	 *
	 * @param staticPinRequestDTO the static pin request DTO
	 * @return the static pin response DTO
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	StaticPinResponseDTO storeSpin(StaticPinRequestDTO staticPinRequestDTO) throws IdAuthenticationBusinessException;

}
