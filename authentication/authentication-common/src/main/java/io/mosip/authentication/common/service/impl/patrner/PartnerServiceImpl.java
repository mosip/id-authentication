package io.mosip.authentication.common.service.impl.patrner;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.authentication.common.service.integration.PartnerServiceManager;
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
	
	@Autowired(required = false)
	private PartnerServiceManager partnerServiceManager;
	
	ConcurrentHashMap<PartnerDTO,PartnerPolicyResponseDTO> partnerServiceResponseMap = new ConcurrentHashMap<>();
	
	/* (non-Javadoc)
	 * @see io.mosip.authentication.core.spi.partner.service.PartnerService#getPartner(java.lang.String)
	 */
	public Optional<PartnerDTO> getPartner(String partnerId) throws IdAuthenticationBusinessException {
		return partnerServiceResponseMap.keySet()
				.stream()
				.filter(partner -> partner.getPartnerId().equals(partnerId))
				.findAny();
	}

	private PartnerDTO createPartnerDTO(PartnerPolicyResponseDTO partnerPolicyDTO, String partnerApiKey) {
		PartnerDTO partnerDTO = new PartnerDTO();
		partnerDTO.setPartnerId(partnerPolicyDTO.getPartnerId());
		partnerDTO.setPartnerApiKey(partnerApiKey);
		partnerDTO.setPartnerName(partnerPolicyDTO.getPartnerName());
		partnerDTO.setPolicyId(partnerPolicyDTO.getPolicyId());
		partnerDTO.setStatus("Active");
		return partnerDTO;
	}	

	@Override
	public PartnerPolicyResponseDTO validateAndGetPolicy(String partnerId, String partnerApiKey, String mispLicenseKey) throws IdAuthenticationBusinessException {
		PartnerDTO key = new PartnerDTO(partnerId, partnerApiKey);
		if (partnerServiceResponseMap.containsKey(key)) {
			return partnerServiceResponseMap.get(key);
		} else {
			PartnerPolicyResponseDTO partnerPolicyResponseDTO = partnerServiceManager.validateAndGetPolicy(partnerId, partnerApiKey, mispLicenseKey);
			partnerServiceResponseMap.put(createPartnerDTO(partnerPolicyResponseDTO, partnerApiKey), partnerPolicyResponseDTO);
			return partnerPolicyResponseDTO;
		}
	}


	@Override
	public Optional<PolicyDTO> getPolicyForPartner(String partnerId, String partnerApiKey) throws IdAuthenticationBusinessException {
		PartnerDTO key = new PartnerDTO(partnerId, partnerApiKey);
		if(partnerServiceResponseMap.containsKey(key)) {
			return Optional.ofNullable(partnerServiceResponseMap.get(key))
					.map(PartnerPolicyResponseDTO::getPolicy);
		}
		return Optional.empty();
	}
}
