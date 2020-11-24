package io.mosip.authentication.common.service.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.integration.PartnerServiceManager;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.partner.dto.PartnerDTO;
import io.mosip.authentication.core.partner.dto.PartnerPolicyResponseDTO;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * The Class PartnerServiceCache.
 *
 * @author Manoj SP
 */
@Component
public class PartnerServiceCache {

	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(PartnerServiceCache.class);

	/** The partner service manager. */
	@Autowired(required = false)
	private PartnerServiceManager partnerServiceManager;

	/**
	 * Gets the partner policy.
	 *
	 * @param partner        the partner
	 * @param mispLicenseKey the misp license key
	 * @return the partner policy
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	@Cacheable(cacheNames = "partner", key = "#partner")
	public PartnerPolicyResponseDTO getPartnerPolicy(PartnerDTO partner, String mispLicenseKey, boolean certificateNeeded)
			throws IdAuthenticationBusinessException {
		PartnerPolicyResponseDTO validateAndGetPolicy = partnerServiceManager.validateAndGetPolicy(partner.getPartnerId(), partner.getPartnerApiKey(),
						mispLicenseKey);
		if(certificateNeeded) {
			String partnerCertificate = partnerServiceManager.getPartnerCertificate(partner.getPartnerId());
			if(partnerCertificate == null) {
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.PARTNER_CERT_NOT_AVAILABLE);
			}
			validateAndGetPolicy.setPartnerCertificate(partnerCertificate);
		}
		return validateAndGetPolicy;
	}

	@CacheEvict(cacheNames = "partner")
	public void evictPartnerPolicy(PartnerDTO partner) {
	}

	/**
	 * Clear partner service cache.
	 */
	@CacheEvict(value = "partner", allEntries = true)
	public void clearPartnerServiceCache() {
		logger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "clearPartnerServiceCache",
				"partner cache cleared");
	}

}
