package io.mosip.authentication.common.service.impl.patrner;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.cache.PartnerServiceCache;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
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
	private PartnerServiceCache partnerServiceCache;
	
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
	public PartnerPolicyResponseDTO validateAndGetPolicy(String partnerId, String partnerApiKey, String mispLicenseKey, boolean certificateNeeded)
			throws IdAuthenticationBusinessException {
		PartnerDTO key = new PartnerDTO(partnerId, partnerApiKey, mispLicenseKey);
		PartnerPolicyResponseDTO partnerPolicyResponseDTO = partnerServiceCache.getPartnerPolicy(key, mispLicenseKey, certificateNeeded);
		
		if(isExpired(partnerPolicyResponseDTO.getMispExpiresOn())) {
			partnerServiceCache.evictPartnerPolicy(key);
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.MISP_LICENSE_KEY_EXPIRED);
		}
		
		if(isExpired(partnerPolicyResponseDTO.getApiKeyExpiresOn()) || isExpired(partnerPolicyResponseDTO.getPolicyExpiresOn())) {
			partnerServiceCache.evictPartnerPolicy(key);
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_POLICY_ID);
		}
		
		return partnerPolicyResponseDTO;
	}

	private boolean isExpired(LocalDateTime expiryDateTime) {
		return expiryDateTime == null || LocalDateTime.now().isAfter(expiryDateTime);
	}

	@Override
	public Optional<PartnerPolicyResponseDTO> getPolicyForPartner(String partnerId, String partnerApiKey, Map<String, Object> metadata)
			throws IdAuthenticationBusinessException {
		String key = partnerId + partnerApiKey;
		return Optional.ofNullable(mapper.convertValue(metadata.get(key), PartnerPolicyResponseDTO.class));
	}
}
