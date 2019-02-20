package io.mosip.authentication.core.spi.id.service;

import io.mosip.authentication.core.dto.vid.VIDResponseDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

/**
 * The Interface VIDService.
 * 
 * @author Arun Bose
 */
public interface VIDService {
	
	/**
	 * this method generates the VID based on the UIN and performs UIN and VID validations,prior to the generation
	 *
	 * @param UIN the uin
	 * @return the VID response DTO
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	VIDResponseDTO generateVID(String uin) throws IdAuthenticationBusinessException;

}
