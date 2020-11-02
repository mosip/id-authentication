package io.mosip.authentication.common.service.impl.patrner;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import io.mosip.authentication.common.service.cache.PartnerServiceCache;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.partner.dto.PartnerDTO;
import io.mosip.authentication.core.partner.dto.PartnerPolicyResponseDTO;
import io.mosip.authentication.core.partner.dto.PolicyDTO;
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
	private CacheManager cacheManager;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.core.spi.partner.service.PartnerService#getPartner(
	 * java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public Optional<PartnerDTO> getPartner(String partnerId) throws IdAuthenticationBusinessException {
		Map<PartnerDTO, PartnerPolicyResponseDTO> partnerCacheMap = (Map<PartnerDTO, PartnerPolicyResponseDTO>) cacheManager
				.getCache("partner").getNativeCache();
		Optional<PartnerDTO> partnerDTO = partnerCacheMap.keySet().stream()
				.filter(partner -> partner.getPartnerId().equals(partnerId)).findAny();
		partnerDTO.ifPresent(partner -> {
			PartnerPolicyResponseDTO partnerPolicyResponseDTO = partnerCacheMap.get(partner);
			partner.setPartnerName(partnerPolicyResponseDTO.getPartnerName());
			partner.setPolicyId(partnerPolicyResponseDTO.getPolicyId());
			partner.setStatus("Active");
		});
		return partnerDTO;
	}

	@Override
	public PartnerPolicyResponseDTO validateAndGetPolicy(String partnerId, String partnerApiKey, String mispLicenseKey)
			throws IdAuthenticationBusinessException {
		PartnerDTO key = new PartnerDTO(partnerId, partnerApiKey);
//		if (partnerServiceResponseMap.containsKey(key)) {
//			return partnerServiceResponseMap.get(key);
//		} else {
			PartnerPolicyResponseDTO partnerPolicyResponseDTO = partnerServiceCache.getPartnerPolicy(key, mispLicenseKey);
//			partnerServiceResponseMap.put(createPartnerDTO(partnerPolicyResponseDTO, partnerApiKey), partnerPolicyResponseDTO);
			return partnerPolicyResponseDTO;
//		}
	}

	@Override
	public Optional<PolicyDTO> getPolicyForPartner(String partnerId, String partnerApiKey)
			throws IdAuthenticationBusinessException {
		PartnerDTO key = new PartnerDTO(partnerId, partnerApiKey);
//		if (partnerServiceResponseMap.containsKey(key)) {
			return Optional.ofNullable(partnerServiceCache.getPartnerPolicy(key, null)).map(PartnerPolicyResponseDTO::getPolicy);
//		}
//		return Optional.empty();
	}

//	private PartnerDTO createPartnerDTO(PartnerPolicyResponseDTO partnerPolicyDTO, String partnerApiKey) {
//		PartnerDTO partnerDTO = new PartnerDTO();
//		partnerDTO.setPartnerId(partnerPolicyDTO.getPartnerId());
//		partnerDTO.setPartnerApiKey(partnerApiKey);
//		partnerDTO.setPartnerName(partnerPolicyDTO.getPartnerName());
//		partnerDTO.setPolicyId(partnerPolicyDTO.getPolicyId());
//		partnerDTO.setStatus("Active");
//		return partnerDTO;
//	}
}
