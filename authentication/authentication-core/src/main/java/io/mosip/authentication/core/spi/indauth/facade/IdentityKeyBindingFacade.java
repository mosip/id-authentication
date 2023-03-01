package io.mosip.authentication.core.spi.indauth.facade;

import java.util.Map;

import io.mosip.authentication.core.dto.ObjectWithMetadata;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.IdentityKeyBindingRequestDTO;
import io.mosip.authentication.core.indauth.dto.IdentityKeyBindingResponseDto;

/**
 * This class used to integrate with identity key binding
 * 
 * @author Mahammed Taheer
 */
public interface IdentityKeyBindingFacade {
    
    /**
	 * Authenticate individual.
	 *
	 * @param authRequest the auth request
	 * @param partnerId the partner id
	 * @param partnerApiKey the partner api key id
	 * @param requestWithMetadata the request object with metadata
	 * @return the auth response DTO
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 * @throws IdAuthenticationDaoException the id authentication dao exception
	 */
	AuthResponseDTO authenticateIndividual(AuthRequestDTO authRequest, String partnerId, String partnerApiKey, 
                    ObjectWithMetadata requestWithMetadata)	throws IdAuthenticationBusinessException, IdAuthenticationDaoException;
    
    /**
	 * Process the IdentityKeyBindingRequestDTO to integrate with KYCService.
	 *
	 * @param identityKeyBindingRequestDTO is DTO of IdentityKeyBindingRequestDTO
	 * @param partnerId the partner id
	 * @param oidcClientId the client id
	 * @param metadata the metadata
	 * @return IdentityKeyBindingResponseDto the identity key binding response DTO
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 * 
	 */
	IdentityKeyBindingResponseDto processIdentityKeyBinding(IdentityKeyBindingRequestDTO identityKeyBindingRequestDTO, AuthResponseDTO authResponseDTO,
                String partnerId, String oidcClientId, Map<String, Object>  metadata) throws IdAuthenticationBusinessException;

}
