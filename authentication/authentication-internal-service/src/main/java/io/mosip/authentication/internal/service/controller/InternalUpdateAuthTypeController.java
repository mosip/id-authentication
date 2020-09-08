package io.mosip.authentication.internal.service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.authtype.status.service.UpdateAuthtypeStatusService;
import io.mosip.idrepository.core.dto.IDAEventDTO;
import io.mosip.idrepository.core.dto.IDAEventsDTO;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.websub.api.annotation.PreAuthenticateContentAndVerifyIntent;

/**
 * The InternalUpdateAuthTypeController use to fetch Auth Transaction.
 *
 * @author Dinesh Karuppiah.T
 */
@RestController
public class InternalUpdateAuthTypeController {

	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(InternalUpdateAuthTypeController.class);

	@Autowired
	private UpdateAuthtypeStatusService authtypeStatusService;

	@Autowired
	private AuditHelper auditHelper;
	
	@GetMapping(value = "/authTypeCallback")
	public ResponseEntity<String> updateAuthtypeStatusIntentVerifier(
			@RequestParam(name = "hub.mode", required = true) String mode,
			@RequestParam(name = "hub.topic", required = true) String topic,
			@RequestParam(name = "hub.challenge", required = true) String challenge,
			@RequestParam(name = "hub.lease_seconds", required = false) String leaseSecs
			)
			throws IdAuthenticationAppException, IDDataValidationException {
		logger.debug(IdAuthCommonConstants.SESSION_ID, "updateAuthtypeStatusIntentVerifier", "", "inside Intent verifier of credentialIssueanceCallback \n "
				+ "mode: " + mode + "\n"
				+ "topic: " + topic + "\n"
				+ "challenge: " + challenge + "\n"
				+ "lease_seconds: " + leaseSecs);
		return ResponseEntity.ok().body(challenge);
	}

	@PostMapping(value = "/authTypeCallback", consumes = "application/json")
	@PreAuthenticateContentAndVerifyIntent(secret = "Kslk30SNF2AChs2", callback = "/authTypeCallback", topic = "AUTH_TYPE_STATUS_UPDATE")
	public void updateAuthtypeStatus(IDAEventsDTO events)
			throws IdAuthenticationAppException, IDDataValidationException {
		try {
			List<IDAEventDTO> eventsList = events.getEvents();
			for (IDAEventDTO event : eventsList) {
				authtypeStatusService.updateAuthTypeStatus(event.getTokenId(), event.getAuthTypeStatusList());

				auditHelper.audit(AuditModules.AUTH_TYPE_STATUS, AuditEvents.UPDATE_AUTH_TYPE_STATUS_REQUEST_RESPONSE,
						event.getTokenId(), IdType.UIN, "internal auth type status update status : " + true);
			}
		} catch (IdAuthenticationBusinessException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, e.getClass().toString(), e.getErrorCode(), e.getErrorText());
			auditHelper.audit(AuditModules.AUTH_TYPE_STATUS, AuditEvents.UPDATE_AUTH_TYPE_STATUS_REQUEST_RESPONSE,
					events.getEvents().get(0).getTokenId(), IdType.UIN, e);
			throw new IdAuthenticationAppException(e.getErrorCode(), e.getErrorText(), e);
		}

	}

}
