package io.mosip.authentication.common.service.impl.patrner;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.integration.PartnerServiceManager;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.partner.dto.PartnerDTO;
import io.mosip.authentication.core.partner.dto.PartnerPolicyResponseDTO;
import io.mosip.authentication.core.spi.partner.service.PartnerService;

/**
 * The Implementation of PartnerService to fetch the Partner, Policy, MISP and
 * License information. Any caching of these data to be handled here.
 * 
 * @author Loganathan Sekar
 * @author Nagarjuna K
 *
 */
@Service
public class PartnerServiceImpl implements PartnerService {

	@Autowired
	private PartnerServiceManager partnerServiceManager;
	
	@Autowired
	private ObjectMapper mapper;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.core.spi.partner.service.PartnerService#getPartner(
	 * java.lang.String)
	 */
	@Override
	public Optional<PartnerDTO> getPartner(String partnerId, Map<String, Object> metadata)
			throws IdAuthenticationBusinessException {
		return Optional.ofNullable(mapper.convertValue(metadata.get(partnerId), PartnerDTO.class));
	}

	@Override
	public PartnerPolicyResponseDTO validateAndGetPolicy(String partnerId, String partnerApiKey, String mispLicenseKey, 
			boolean certificateNeeded, String headerCertificateThumbprint, boolean certValidationNeeded)
			throws IdAuthenticationBusinessException {
		return partnerServiceManager.validateAndGetPolicy(partnerId, partnerApiKey, mispLicenseKey, 
					certificateNeeded, headerCertificateThumbprint, certValidationNeeded);
	}

	@Override
	public Optional<PartnerPolicyResponseDTO> getPolicyForPartner(String partnerId, String partnerApiKey, Map<String, Object> metadata)
			throws IdAuthenticationBusinessException {
		String key = partnerId + partnerApiKey;
		return Optional.ofNullable(mapper.convertValue(metadata.get(key), PartnerPolicyResponseDTO.class));
	}
}
