package io.mosip.authentication.kyc.service.controller;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.APIKEY_APPROVED;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.MISP_LICENSE_GENERATED;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.MISP_LICENSE_UPDATED;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.PARTNER_API_KEY_UPDATED_EVENT_NAME;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.PARTNER_UPDATED_EVENT_NAME;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.POLICY_UPDATED_EVENT_NAME;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_PARTNER_SERVICE_CALLBACK_SECRET;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_TOPIC_PMP_MISP_LICENSE_GENERATED;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_TOPIC_PMP_MISP_LICENSE_UPDATED;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_TOPIC_PMP_PARTNER_API_KEY_APPROVED;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_TOPIC_PMP_PARTNER_API_KEY_UPDATED;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_TOPIC_PMP_PARTNER_UPDATED;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_TOPIC_PMP_POLICY_UPDATED;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.authentication.common.service.integration.PartnerServiceManager;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.websub.model.EventModel;
import io.mosip.kernel.websub.api.annotation.PreAuthenticateContentAndVerifyIntent;

/**
 * The Class PartnerServiceCallbackController.
 * 
 * @author Manoj SP
 */
@RestController
public class PartnerServiceCallbackController {

	private static final Logger logger = IdaLogger.getLogger(PartnerServiceCallbackController.class);

	/** The controller delegate. */
	@Autowired
	private PartnerServiceManager partnerManager;

	@Autowired
	private IdAuthSecurityManager securityManager;

	@PostMapping(value = "/callback/partnermanagement/" + APIKEY_APPROVED, consumes = "application/json")
	@PreAuthenticateContentAndVerifyIntent(secret = "${" + IDA_WEBSUB_PARTNER_SERVICE_CALLBACK_SECRET
			+ "}", callback = "/idauthentication/v1/auth/callback/partnermanagement/"
					+ APIKEY_APPROVED, topic = "${" + IDA_WEBSUB_TOPIC_PMP_PARTNER_API_KEY_APPROVED + "}")
	public void handleApiKeyApprovedEvent(@RequestBody EventModel eventModel) {
		try {
			eventModel.setTopic(APIKEY_APPROVED);
			logger.debug(securityManager.getUser(), "PartnerServiceCallbackController", "handleApiKeyApprovedEvent",
					APIKEY_APPROVED + " EVENT RECEIVED");
			partnerManager.updateApiKeyData(eventModel);
		} catch (Exception e) {
			logger.error(securityManager.getUser(), "PartnerServiceCallbackController", "handleApiKeyApprovedEvent",
					StringUtils.arrayToDelimitedString(ExceptionUtils.getRootCauseStackTrace(e), "\n"));
		}
	}

	@PostMapping(value = "/callback/partnermanagement/" + PARTNER_UPDATED_EVENT_NAME, consumes = "application/json")
	@PreAuthenticateContentAndVerifyIntent(secret = "${" + IDA_WEBSUB_PARTNER_SERVICE_CALLBACK_SECRET
			+ "}", callback = "/idauthentication/v1/auth/callback/partnermanagement/"
					+ PARTNER_UPDATED_EVENT_NAME, topic = "${" + IDA_WEBSUB_TOPIC_PMP_PARTNER_UPDATED + "}")
	public void handlePartnerUpdated(@RequestBody EventModel eventModel) {
		try {
			logger.debug(securityManager.getUser(), "PartnerServiceCallbackController", "handlePartnerUpdated",
					PARTNER_UPDATED_EVENT_NAME + " EVENT RECEIVED");
			partnerManager.updatePartnerData(eventModel);
		} catch (Exception e) {
			logger.error(securityManager.getUser(), "PartnerServiceCallbackController", "handlePartnerUpdated",
					StringUtils.arrayToDelimitedString(ExceptionUtils.getRootCauseStackTrace(e), "\n"));
		}
	}

	@PostMapping(value = "/callback/partnermanagement/" + POLICY_UPDATED_EVENT_NAME, consumes = "application/json")
	@PreAuthenticateContentAndVerifyIntent(secret = "${" + IDA_WEBSUB_PARTNER_SERVICE_CALLBACK_SECRET
			+ "}", callback = "/idauthentication/v1/auth/callback/partnermanagement/"
					+ POLICY_UPDATED_EVENT_NAME, topic = "${" + IDA_WEBSUB_TOPIC_PMP_POLICY_UPDATED + "}")
	public void handlePolicyUpdated(@RequestBody EventModel eventModel) {
		try {
			logger.debug(securityManager.getUser(), "PartnerServiceCallbackController", "handlePolicyUpdated",
					POLICY_UPDATED_EVENT_NAME + " EVENT RECEIVED");
			partnerManager.updatePolicyData(eventModel);
		} catch (Exception e) {
			logger.error(securityManager.getUser(), "PartnerServiceCallbackController", "handlePolicyUpdated",
					StringUtils.arrayToDelimitedString(ExceptionUtils.getRootCauseStackTrace(e), "\n"));
		}
	}

	@PostMapping(value = "/callback/partnermanagement/" + PARTNER_API_KEY_UPDATED_EVENT_NAME, consumes = "application/json")
	@PreAuthenticateContentAndVerifyIntent(secret = "${" + IDA_WEBSUB_PARTNER_SERVICE_CALLBACK_SECRET
			+ "}", callback = "/idauthentication/v1/auth/callback/partnermanagement/"
					+ PARTNER_API_KEY_UPDATED_EVENT_NAME, topic = "${" + IDA_WEBSUB_TOPIC_PMP_PARTNER_API_KEY_UPDATED + "}")
	public void handlePartnerApiKeyUpdated(@RequestBody EventModel eventModel) {
		try {
			logger.debug(securityManager.getUser(), "PartnerServiceCallbackController", "handlePartnerApiKeyUpdated",
					PARTNER_API_KEY_UPDATED_EVENT_NAME + " EVENT RECEIVED");
			partnerManager.updateApiKeyData(eventModel);
		} catch (Exception e) {
			logger.error(securityManager.getUser(), "PartnerServiceCallbackController", "handlePartnerApiKeyUpdated",
					StringUtils.arrayToDelimitedString(ExceptionUtils.getRootCauseStackTrace(e), "\n"));
		}
	}

	@PostMapping(value = "/callback/partnermanagement/" + MISP_LICENSE_GENERATED, consumes = "application/json")
	@PreAuthenticateContentAndVerifyIntent(secret = "${" + IDA_WEBSUB_PARTNER_SERVICE_CALLBACK_SECRET
			+ "}", callback = "/idauthentication/v1/auth/callback/partnermanagement/"
					+ MISP_LICENSE_GENERATED, topic = "${" + IDA_WEBSUB_TOPIC_PMP_MISP_LICENSE_GENERATED + "}")
	public void handleMispLicenseGeneratedEvent(@RequestBody EventModel eventModel) {
		try {
			logger.debug(securityManager.getUser(), "PartnerServiceCallbackController", "handleMispLicenseGeneratedEvent",
					MISP_LICENSE_GENERATED + " EVENT RECEIVED");
			partnerManager.updateMispLicenseData(eventModel);
		} catch (Exception e) {
			logger.error(securityManager.getUser(), "PartnerServiceCallbackController", "handleMispLicenseGeneratedEvent",
					StringUtils.arrayToDelimitedString(ExceptionUtils.getRootCauseStackTrace(e), "\n"));
		}
	}

	@PostMapping(value = "/callback/partnermanagement/" + MISP_LICENSE_UPDATED, consumes = "application/json")
	@PreAuthenticateContentAndVerifyIntent(secret = "${" + IDA_WEBSUB_PARTNER_SERVICE_CALLBACK_SECRET
			+ "}", callback = "/idauthentication/v1/auth/callback/partnermanagement/"
					+ MISP_LICENSE_UPDATED, topic = "${" + IDA_WEBSUB_TOPIC_PMP_MISP_LICENSE_UPDATED + "}")
	public void handleMispUpdatedEvent(@RequestBody EventModel eventModel) {
		try {
			logger.debug(securityManager.getUser(), "PartnerServiceCallbackController", "handleMispUpdatedEvent",
					MISP_LICENSE_UPDATED + " EVENT RECEIVED");
			partnerManager.updateMispLicenseData(eventModel);
		} catch (Exception e) {
			logger.error(securityManager.getUser(), "PartnerServiceCallbackController", "handleMispUpdatedEvent",
					StringUtils.arrayToDelimitedString(ExceptionUtils.getRootCauseStackTrace(e), "\n"));
		}
	}
}
