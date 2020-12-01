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
import io.mosip.idrepository.core.dto.EventModel;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * The callback delegate for handling partner service update
 * 
 * @author Manoj SP
 *
 */
@Component
public class PartnerServiceCallbackControllerDelegate {
	
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
		if (data.containsKey("partnerId") && data.get("partnerId") instanceof String) {
			partnerManager.evictPartnerBasedOnPartnerId((String) data.get("partnerId"));
		}
		if (data.containsKey("partnerApiKey") && data.get("partnerApiKey") instanceof String) {
			partnerManager.evictPartnerBasedOnPartnerApiKey((String) data.get("partnerApiKey"));
		}
		if (data.containsKey("mispLicenseKey") && data.get("mispLicenseKey") instanceof String) {
			partnerManager.evictPartnerBasedOnMispLicenseKey((String) data.get("mispLicenseKey"));
		}
		if (data.containsKey("policyId") && data.get("policyId") instanceof String) {
			partnerManager.evictPartnerBasedOnPolicyId((String) data.get("policyId"));
		}
	}
}
