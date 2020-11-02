package io.mosip.authentication.common.service.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.integration.PartnerServiceManager;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.partner.dto.PartnerDTO;
import io.mosip.authentication.core.partner.dto.PartnerPolicyResponseDTO;

/**
 * @author Manoj SP
 *
 */
@Component
public class PartnerServiceCache {

	@Autowired(required = false)
	private PartnerServiceManager partnerServiceManager;

	@Cacheable(cacheNames = "partner", key = "#partner")
	public PartnerPolicyResponseDTO getPartnerPolicy(PartnerDTO partner, String mispLicenseKey)
			throws IdAuthenticationBusinessException {
		return partnerServiceManager.validateAndGetPolicy(partner.getPartnerId(), partner.getPartnerApiKey(),
				mispLicenseKey);
	}
	
	@CacheEvict(value="partner", allEntries=true)
	public void clearPartnerServiceCache() {
		
	}

}
