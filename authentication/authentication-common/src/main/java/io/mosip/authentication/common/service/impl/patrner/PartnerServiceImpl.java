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
	
	ConcurrentHashMap<String,PartnerPolicyResponseDTO> partnerServiceResponseMap = new ConcurrentHashMap<String, PartnerPolicyResponseDTO>();
	
	/* (non-Javadoc)
	 * @see io.mosip.authentication.core.spi.partner.service.PartnerService#getPartner(java.lang.String)
	 */
	public Optional<PartnerDTO> getPartner(String partnerId) throws IdAuthenticationBusinessException {
		
		if(partnerServiceResponseMap.containsKey(partnerId)) {
			PartnerPolicyResponseDTO partnerPolicyDTO = partnerServiceResponseMap.get(partnerId);
			PartnerDTO partnerDTO = new PartnerDTO();
			partnerDTO.setPartnerId(partnerPolicyDTO.getPartnerId());
			partnerDTO.setPartnerName(partnerPolicyDTO.getPartnerName());
			partnerDTO.setPolicyId(partnerPolicyDTO.getPolicyId());
			partnerDTO.setStatus("Active");
			return Optional.of(partnerDTO);
		}
		
		return Optional.empty();
	}	

	@Override
	public PartnerPolicyResponseDTO validateAndGetPolicy(String partnerId, String partner_api_key, String misp_license_key) throws IdAuthenticationBusinessException {
		PartnerPolicyResponseDTO response;
			response = partnerServiceManager.validateAndGetPolicy(partnerId, partner_api_key, misp_license_key);			
		partnerServiceResponseMap.putIfAbsent(response.getPartnerId(), response);		
		return response;
	}


	@Override
	public PolicyDTO getPolicyForPartner(String partnerId) throws IdAuthenticationBusinessException {
		// TODO Auto-generated method stub
		return null;
	}
}
