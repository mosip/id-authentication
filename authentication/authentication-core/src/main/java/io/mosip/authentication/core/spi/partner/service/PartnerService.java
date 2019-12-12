package io.mosip.authentication.core.spi.partner.service;

import java.util.Optional;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.partner.dto.License;
import io.mosip.authentication.core.partner.dto.PartnerDTO;
import io.mosip.authentication.core.partner.dto.PolicyDTO;

/**
 * The service to obtain MISP and Partner details.
 * 
 * @author Loganathan Sekar
 *
 */
public interface PartnerService {
	
	Optional<PolicyDTO> getPolicy(String policyId) throws IdAuthenticationBusinessException;
	
	Optional<PartnerDTO> getPartner(String partnerId) throws IdAuthenticationBusinessException;
	
	Optional<License> getLicense(String licenseKey) throws IdAuthenticationBusinessException;
	
	boolean hasMispPartnerMapping(String partnerId, String mispId) throws IdAuthenticationBusinessException;

}
