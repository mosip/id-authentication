package io.mosip.authentication.otp.service.controller;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.MISP_UPDATED_EVENT_NAME;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.PARTNER_API_KEY_UPDATED_EVENT_NAME;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.PARTNER_UPDATED_EVENT_NAME;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.POLICY_UPDATED_EVENT_NAME;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_PARTNER_SERVICE_CALLBACK_SECRET;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_TOPIC_PMP_MISP_UPDATED;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_TOPIC_PMP_PARTNER_API_KEY_UPDATED;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_TOPIC_PMP_PARTNER_UPDATED;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_TOPIC_PMP_POLICY_UPDATED;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.authentication.common.service.controller.PartnerServiceCallbackControllerDelegate;
import io.mosip.kernel.core.websub.model.EventModel;
import io.mosip.kernel.websub.api.annotation.PreAuthenticateContentAndVerifyIntent;

/**
 * The Class PartnerServiceCallbackController.
 * @author Loganathan Sekar
 */
@RestController
public class PartnerServiceCallbackController {

	/** The controller delegate. */
	@Autowired
	private PartnerServiceCallbackControllerDelegate controllerDelegate;

	@PostMapping(value = "/callback/partnermanagement/" + MISP_UPDATED_EVENT_NAME, consumes = "application/json")
	@PreAuthenticateContentAndVerifyIntent(secret = "${" + IDA_WEBSUB_PARTNER_SERVICE_CALLBACK_SECRET
			+ "}", callback = "/idauthentication/v1/otp/callback/partnermanagement/" + MISP_UPDATED_EVENT_NAME, topic = "${" + IDA_WEBSUB_TOPIC_PMP_MISP_UPDATED + "}")
	public void handleMispUpdatedEvent(@RequestBody EventModel eventModel) {
		controllerDelegate.updatePartnerInfo(eventModel);
	}
	
	@PostMapping(value = "/callback/partnermanagement/" + PARTNER_UPDATED_EVENT_NAME, consumes = "application/json")
	@PreAuthenticateContentAndVerifyIntent(secret = "${" + IDA_WEBSUB_PARTNER_SERVICE_CALLBACK_SECRET
			+ "}", callback = "/idauthentication/v1/otp/callback/partnermanagement/" + PARTNER_UPDATED_EVENT_NAME, topic = "${" + IDA_WEBSUB_TOPIC_PMP_PARTNER_UPDATED + "}")
	public void handlePartnerUpdated(@RequestBody EventModel eventModel) {
		controllerDelegate.updatePartnerInfo(eventModel);
	}
	
	@PostMapping(value = "/callback/partnermanagement/" + PARTNER_API_KEY_UPDATED_EVENT_NAME, consumes = "application/json")
	@PreAuthenticateContentAndVerifyIntent(secret = "${" + IDA_WEBSUB_PARTNER_SERVICE_CALLBACK_SECRET
			+ "}", callback = "/idauthentication/v1/otp/callback/partnermanagement/" + PARTNER_API_KEY_UPDATED_EVENT_NAME, topic = "${" + IDA_WEBSUB_TOPIC_PMP_PARTNER_API_KEY_UPDATED + "}")
	public void handlePartnerApiKeyUpdated(@RequestBody EventModel eventModel) {
		controllerDelegate.updatePartnerInfo(eventModel);
	}
	
	@PostMapping(value = "/callback/partnermanagement/" + POLICY_UPDATED_EVENT_NAME, consumes = "application/json")
	@PreAuthenticateContentAndVerifyIntent(secret = "${" + IDA_WEBSUB_PARTNER_SERVICE_CALLBACK_SECRET
			+ "}", callback = "/idauthentication/v1/otp/callback/partnermanagement/" + POLICY_UPDATED_EVENT_NAME, topic = "${" + IDA_WEBSUB_TOPIC_PMP_POLICY_UPDATED + "}")
	public void handlePolicyUpdated(@RequestBody EventModel eventModel) {
		controllerDelegate.updatePartnerInfo(eventModel);
	}
}
