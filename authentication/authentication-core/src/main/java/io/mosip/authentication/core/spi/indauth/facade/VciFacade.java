package io.mosip.authentication.core.spi.indauth.facade;

import java.util.Map;

import io.mosip.authentication.core.dto.ObjectWithMetadata;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.VciExchangeRequestDTO;
import io.mosip.authentication.core.indauth.dto.VciExchangeResponseDTO;

/**
 * This class used to integrate with VCI service
 * 
 * @author Mahammed Taheer
 */
public interface VciFacade {
	
	/**
	 * Process the VciExchangeRequestDTO to integrate with VciService.
	 *
	 * @param vciExchangeRequestDTO is DTO of VciExchangeRequestDTO
	 * @param partnerId the partner id
	 * @param oidcClientId the client id
	 * @param metadata the metadata
	 * @param requestWithMetadata the request with metadata
	 * @return the VCI Exchange response DTO
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	VciExchangeResponseDTO processVciExchange(VciExchangeRequestDTO vciExchangeRequestDTO, 
			String partnerId, String oidcClientId, Map<String, Object>  metadata, ObjectWithMetadata requestWithMetadata) throws IdAuthenticationBusinessException;

}
