package io.mosip.authentication.core.spi.indauth.service;

import java.util.List;
import java.util.Map;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.IdentityKeyBindingRequestDTO;

/**
 * This interface is used to generate certificate for 
 * Identity Key Binding
 * 
 * @author Mahammed Taheer
 */

public interface IdentityKeyBindingService {
    
    /**
	 * Method used to check whether input public key is already binded to existing VID for the an Identity
	 *
	 * @param idVid                 the id or vid
	 * @param publicKeyJWK          the public key to be binded
	 * @return boolean              true if public key exist else false.
	 * @throws IdAuthenticationBusinessException the id authentication business exception
     * 
	 */
	boolean isPublicKeyBinded(String idVid, Map<String, Object> publicKeyJWK) throws IdAuthenticationBusinessException;

    /**
	 * Method used to create certificate for the input public key for key binding
	 *
	 * @param identityKeyBindingRequestDTO                the key binding request DTO
	 * @param identityInfo                                the authenticated identity info
     * @param token                                       the identity token
	 * @return String                                     PEM Formatted created certificate.
	 * @throws IdAuthenticationBusinessException the id authentication business exception
     * 
	 */
	String createAndSaveKeyBindingCertificate(IdentityKeyBindingRequestDTO identityKeyBindingRequestDTO, 
                    Map<String, List<IdentityInfoDTO>> identityInfo, String token, String partnerId) throws IdAuthenticationBusinessException;
}
