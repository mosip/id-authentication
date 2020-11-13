package io.mosip.authentication.kyc.service.controller;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_PARTNER_SERVICE_CALLBACK_SECRET;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.authentication.common.service.controller.PartnerServiceCallbackControllerDelegate;
import io.mosip.idrepository.core.dto.EventModel;
import io.mosip.kernel.websub.api.annotation.PreAuthenticateContentAndVerifyIntent;

/**
 * The Class KycPartnerServiceCallbackController.
 */
@RestController
public class KycPartnerServiceCallbackController {

	/** The controller delegate. */
	@Autowired
	private PartnerServiceCallbackControllerDelegate controllerDelegate;

	/**
	 * Update partner info.
	 *
	 * @param eventModel the event model
	 */
	@PostMapping(value = "/callback/partnerServiceCallback", consumes = "application/json")
	@PreAuthenticateContentAndVerifyIntent(secret = "${" + IDA_WEBSUB_PARTNER_SERVICE_CALLBACK_SECRET
			+ "}", callback = "/idauthentication/v1/kyc/callback/partnerServiceCallback", topic = "${ida-topic-partner-service-update}")
	public void updatePartnerInfo(@RequestBody EventModel eventModel) {
		controllerDelegate.updatePartnerInfo(eventModel);
	}
}
