package io.mosip.authentication.core.spi.spin.facade;

import io.mosip.authentication.core.dto.spinstore.StaticPinRequestDTO;
import io.mosip.authentication.core.dto.spinstore.StaticPinResponseDTO;
import io.mosip.authentication.core.dto.vid.VIDRequestDTO;
import io.mosip.authentication.core.dto.vid.VIDResponseDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

// TODO: Auto-generated Javadoc
/**
 * The Interface StaticPinFacade.
 *
 * @author Prem Kumar
 */
public interface StaticPinFacade {
	
	/**
	 * Store spin.
	 *
	 * @param staticPinRequestDTO the static pin request DTO
	 * @return the static pin response DTO
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	StaticPinResponseDTO storeSpin(StaticPinRequestDTO staticPinRequestDTO) throws IdAuthenticationBusinessException;
	
	// FIXME this method has to be in refactored facade
	
	
	/**
	 * this method generates the VID based on the UIN and performs UIN and VID validations,prior to the generation
	 *
	 * @param UIN the uin
	 * @return the VID response DTO
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	VIDResponseDTO generateVID(String uin) throws IdAuthenticationBusinessException;
}
