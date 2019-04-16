package io.mosip.authentication.core.spi.staticpin.service;

import io.mosip.authentication.core.dto.spinstore.StaticPinRequestDTO;
import io.mosip.authentication.core.dto.spinstore.StaticPinResponseDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

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
