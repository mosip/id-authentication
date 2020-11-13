package io.mosip.authentication.common.service.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.integration.PartnerServiceManager;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
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
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	@Cacheable(cacheNames = "partner")
	public PartnerPolicyResponseDTO getPartnerPolicy(PartnerDTO partner) throws IdAuthenticationBusinessException {
		return partnerServiceManager.validateAndGetPolicy(partner.getPartnerId(), partner.getPartnerApiKey(),
				partner.getMispLicenseKey());
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
