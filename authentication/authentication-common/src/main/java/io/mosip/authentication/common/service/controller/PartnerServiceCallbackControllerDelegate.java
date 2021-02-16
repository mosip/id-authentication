package io.mosip.authentication.common.service.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.integration.PartnerServiceManager;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.websub.model.EventModel;

/**
 * The callback delegate for handling partner service update
 * 
 * @author Manoj SP
 *
 */
@Component
public class PartnerServiceCallbackControllerDelegate {
	
	private static final String POLICY_ID = "policyId";

	private static final String MISP_LICENSE_KEY = "mispLicenseKey";

	private static final String API_KEY = "apiKey";

	private static final String PARTNER_ID = "partnerId";

	private static final Logger logger = IdaLogger.getLogger(PartnerServiceManager.class);

	@Autowired
	private PartnerServiceManager partnerManager;
	
	@Autowired
	private ObjectMapper mapper;
	


	public void updatePartnerInfo(@RequestBody EventModel eventModel) {
		
		try {
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "handling event: \n",
					mapper.writeValueAsString(eventModel));
		} catch (JsonProcessingException e) {
			// Skipping the error
		}

		Map<String, Object> data = eventModel.getEvent().getData();
		if (data.containsKey(PARTNER_ID) && data.get(PARTNER_ID) instanceof String) {
			partnerManager.evictPartnerBasedOnPartnerId((String) data.get(PARTNER_ID));
		}
		if (data.containsKey(API_KEY) && data.get(API_KEY) instanceof String) {
			partnerManager.evictPartnerBasedOnPartnerApiKey((String) data.get(API_KEY));
		}
		if (data.containsKey(MISP_LICENSE_KEY) && data.get(MISP_LICENSE_KEY) instanceof String) {
			partnerManager.evictPartnerBasedOnMispLicenseKey((String) data.get(MISP_LICENSE_KEY));
		}
		if (data.containsKey(POLICY_ID) && data.get(POLICY_ID) instanceof String) {
			partnerManager.evictPartnerBasedOnPolicyId((String) data.get(POLICY_ID));
		}
	}
}
