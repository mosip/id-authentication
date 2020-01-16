package io.mosip.authentication.core.spi.staticpin.service;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.staticpin.dto.StaticPinRequestDTO;
import io.mosip.authentication.core.staticpin.dto.StaticPinResponseDTO;

/**
 * This StaticPinService provides Storing of Static Pin into the DataBase.
 * 
 * @author Prem Kumar
 *
 */
public interface StaticPinService {

	/**
	 * This implementation StaticPinService is to Store StaticPin.
	 *
	 * @param staticPinRequestDTO the static pin request DTO
	 * @return the static pin response DTO
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	StaticPinResponseDTO storeSpin(StaticPinRequestDTO staticPinRequestDTO) throws IdAuthenticationBusinessException;

}
