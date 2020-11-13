package io.mosip.authentication.common.service.controller;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_PARTNER_SERVICE_CALLBACK_SECRET;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.authentication.common.service.integration.PartnerServiceManager;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.idrepository.core.dto.EventModel;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.websub.api.annotation.PreAuthenticateContentAndVerifyIntent;

@RestController
public class PartnerServiceCallbackController {

	private static Logger logger = IdaLogger.getLogger(PartnerServiceCallbackController.class);

	@Autowired
	private PartnerServiceManager partnerManager;

	@PostMapping(value = "/callback/partnerServiceCallback", consumes = "application/json")
	@PreAuthenticateContentAndVerifyIntent(secret = "${" + IDA_WEBSUB_PARTNER_SERVICE_CALLBACK_SECRET
			+ "}", callback = "/idauthentication/v1/internal/callback/partnerServiceCallback", topic = "${ida-topic-partner-service-update}")
	public void updateAuthtypeStatus(@RequestBody EventModel eventModel) {
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
