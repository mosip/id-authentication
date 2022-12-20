package io.mosip.authentication.core.spi.partner.service;

import java.util.Map;
import java.util.Optional;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.partner.dto.PartnerDTO;
import io.mosip.authentication.core.partner.dto.PartnerPolicyResponseDTO;

/**
 * The service to fetch the Partner, Policy, MISP and License information. Any
 * caching of these data to be handled here.
 * 
 * @author Loganathan Sekar
 * @author Nagarjuna K
 */
public interface PartnerService {

	Optional<PartnerDTO> getPartner(String partnerId, Map<String, Object> metadata)
			throws IdAuthenticationBusinessException;

	PartnerPolicyResponseDTO validateAndGetPolicy(String partnerId, String partner_api_key, String misp_license_key, 
				boolean certificateNeeded, String headerCertificateThumbprint, boolean certValidationNeeded)
			throws IdAuthenticationBusinessException;

	Optional<PartnerPolicyResponseDTO> getPolicyForPartner(String partnerId, String partnerApiKey, Map<String, Object> metadata)
			throws IdAuthenticationBusinessException;
}
