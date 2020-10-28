package io.mosip.authentication.core.spi.partner.service;

import java.util.Optional;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.partner.dto.PartnerDTO;
import io.mosip.authentication.core.partner.dto.PartnerPolicyResponseDTO;
import io.mosip.authentication.core.partner.dto.PolicyDTO;

/**
 * The service to fetch the Partner, Policy, MISP and License information. 
 * Any caching of these data to be handled here.
 * 
 * @author Loganathan Sekar
 * @author Nagarjuna K
 */
public interface PartnerService {
	
	Optional<PartnerDTO> getPartner(String partnerId) throws IdAuthenticationBusinessException;
	
	
	PartnerPolicyResponseDTO validateAndGetPolicy(String partnerId, String partner_api_key, String misp_license_key) throws IdAuthenticationBusinessException;
	
	 Optional<PolicyDTO> getPolicyForPartner(String partnerId, String partnerApiKey) throws IdAuthenticationBusinessException;
}
