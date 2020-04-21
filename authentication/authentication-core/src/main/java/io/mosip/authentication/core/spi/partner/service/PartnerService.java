package io.mosip.authentication.core.spi.partner.service;

import java.util.Optional;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.partner.dto.License;
import io.mosip.authentication.core.partner.dto.PartnerDTO;
import io.mosip.authentication.core.partner.dto.PartnerPolicyResponseDTO;
import io.mosip.authentication.core.partner.dto.PolicyDTO;

/**
 * The service to fetch the Partner, Policy, MISP and License information. 
 * Any caching of these data to be handled here.
 * 
 * @author Loganathan Sekar
 *
 */
public interface PartnerService {
	
	Optional<PolicyDTO> getPolicy(String policyId) throws IdAuthenticationBusinessException;
	
	Optional<PartnerDTO> getPartner(String partnerId) throws IdAuthenticationBusinessException;
	
	Optional<License> getLicense(String licenseKey) throws IdAuthenticationBusinessException;
	
	boolean getMispPartnerMapping(String partnerId, String mispId) throws IdAuthenticationBusinessException;
	
	PartnerPolicyResponseDTO validateAndGetPolicy(String partnerId, String partner_api_key, String misp_license_key) throws IdAuthenticationBusinessException;

}
