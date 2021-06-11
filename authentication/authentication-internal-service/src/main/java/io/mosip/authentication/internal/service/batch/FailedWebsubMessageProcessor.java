package io.mosip.authentication.internal.service.batch;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_AUTH_PARTNER_ID;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.helper.WebSubHelper.FailedMessage;
import io.mosip.authentication.common.service.impl.patrner.PartnerCACertEventService;
import io.mosip.authentication.common.service.integration.PartnerServiceManager;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.PartnerEventTypes;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.authtype.status.service.UpdateAuthtypeStatusService;
import io.mosip.authentication.core.spi.hotlist.service.HotlistService;
import io.mosip.authentication.core.spi.idevent.service.IdChangeEventHandlerService;
import io.mosip.authentication.core.spi.masterdata.MasterDataCacheUpdateService;
import io.mosip.idrepository.core.constant.IDAEventType;
import io.mosip.idrepository.core.dto.IDAEventDTO;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.websub.model.EventModel;

/**
 * The Class FailedWebsubMessageProcessor.
 * 
 * @author Loganathan Sekar
 */
@Component
public class FailedWebsubMessageProcessor {
	
	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(FailedWebsubMessageProcessor.class);
	
	/** The auth parther id. */
	@Value("${"+ IDA_AUTH_PARTNER_ID  +"}")
	private String authPartherId;
	
	/** The credential store service. */
	@Autowired
	private IdChangeEventHandlerService credentialStoreService;
	
	/** The authtype status service. */
	@Autowired
	private UpdateAuthtypeStatusService authtypeStatusService;
	
	/** The hotlist service. */
	@Autowired
	private HotlistService hotlistService;
	
	/** The master data cache update service. */
	@Autowired
	private MasterDataCacheUpdateService masterDataCacheUpdateService;
	
	/** The partner CA cert event service. */
	@Autowired
	private PartnerCACertEventService partnerCACertEventService;
	
	@Autowired
	private PartnerServiceManager partnerManager;
	
	/** The mapper. */
	@Autowired
	private ObjectMapper mapper;
	
	/**
	 * Process failed websub messages.
	 *
	 * @param failedMessages the failed messages
	 */
	public void processFailedWebsubMessages(List<? extends FailedMessage> failedMessages) {
		failedMessages.forEach(this::processFailedMessage);
	}

	/**
	 * Process failed message.
	 *
	 * @param failedMessage the failed message
	 */
	private void processFailedMessage(FailedMessage failedMessage) {
		try {
			doProcessFailedMessage(failedMessage);
		} catch (Exception e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getName(),
					"processFailedMessage", "Error in Processing failedMessage : " + failedMessage.toString() + ": "
							+ ExceptionUtils.getStackTrace(e));
		}
	}
	
	/**
	 * Do process failed message.
	 *
	 * @param failedMessage the failed message
	 * @throws Exception the exception
	 */
	private void doProcessFailedMessage(FailedMessage failedMessage) throws Exception {
		failedMessage.getFailedMessageConsumer().accept(failedMessage);
	}

	/**
	 * Process id change event.
	 *
	 * @param eventType the event type
	 * @param failedMessage the failed message
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	public void processIdChangeEvent(IDAEventType eventType, FailedMessage failedMessage) throws IdAuthenticationBusinessException {
		mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, "processIdChangeEvent", "", "handling " + eventType + " event for partnerId: " + authPartherId);
		EventModel event = getEventModel(failedMessage);
		credentialStoreService.handleIdEvent(event);
	}

	/**
	 * Process auth type status event.
	 *
	 * @param failedMessage the failed message
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	public void processAuthTypeStatusEvent(FailedMessage failedMessage) throws IdAuthenticationAppException {
		try {
			mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, "processAuthTypeStatusEvent", this.getClass().getCanonicalName(), "handling updateAuthtypeStatus event for partnerId: " + authPartherId);

			IDAEventDTO event = mapper.convertValue(failedMessage.getMessage(), IDAEventDTO.class);
			authtypeStatusService.updateAuthTypeStatus(event.getTokenId(), event.getAuthTypeStatusList());

		} catch (IdAuthenticationBusinessException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, e.getClass().getCanonicalName(), e.getErrorCode(), e.getErrorText());
			throw new IdAuthenticationAppException(e.getErrorCode(), e.getErrorText(), e);
		}
	}
	
	/**
	 * Process hotlist event.
	 *
	 * @param failedMessage the failed message
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	public void processHotlistEvent(FailedMessage failedMessage) throws IdAuthenticationBusinessException {
		mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, this.getClass().getCanonicalName(), "processHotlistEvent", "EVENT RECEIVED");
		EventModel eventModel = getEventModel(failedMessage);
		hotlistService.handlingHotlistingEvent(eventModel);
	}

	/**
	 * Process master data templates event.
	 *
	 * @param failedMessage the failed message
	 */
	public void processMasterdataTemplatesEvent(FailedMessage failedMessage) {
		mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, this.getClass().getCanonicalName(), "processMasterdataTemplatesEvent", "EVENT RECEIVED");
		EventModel eventModel = getEventModel(failedMessage);
		masterDataCacheUpdateService.updateTemplates(eventModel);
	}

	/**
	 * Process master data titles event.
	 *
	 * @param failedMessage the failed message
	 */
	public void processMasterdataTitlesEvent(FailedMessage failedMessage) {
		mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, this.getClass().getCanonicalName(), "processMasterdataTitlesEvent", "EVENT RECEIVED");
		EventModel eventModel = getEventModel(failedMessage);
		masterDataCacheUpdateService.updateTemplates(eventModel);
	}

	/**
	 * Process partner CA cert event.
	 *
	 * @param failedMessage the failed message
	 * @throws RestServiceException the rest service exception
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	public void processPartnerCACertEvent(FailedMessage failedMessage) throws RestServiceException, IdAuthenticationBusinessException {
		mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, this.getClass().getCanonicalName(), "processPartnerCACertEvent", "EVENT RECEIVED");
		EventModel eventModel = getEventModel(failedMessage);
		partnerCACertEventService.handleCACertEvent(eventModel);
	}

	/**
	 * Process partner event.
	 *
	 * @param partnerEventType the partner event type
	 * @param failedMessage the failed message
	 */
	public void processPartnerEvent(PartnerEventTypes partnerEventType, FailedMessage failedMessage) {
		try {
			mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, this.getClass().getCanonicalName(),
					"processPartnerEvent", partnerEventType.getName() + " EVENT RECEIVED");
			EventModel eventModel = getEventModel(failedMessage);
			handlePartnerEvent(partnerEventType, eventModel);
		} catch (Exception e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getCanonicalName(),
					"processPartnerEvent",
					StringUtils.arrayToDelimitedString(ExceptionUtils.getRootCauseStackTrace(e), "\n"));
		}
	}

	private EventModel getEventModel(FailedMessage failedMessage) {
		return mapper.convertValue(failedMessage.getMessage(), EventModel.class);
	}

	private void handlePartnerEvent(PartnerEventTypes partnerEventType, EventModel eventModel)
			throws JsonParseException, JsonMappingException, IOException {
		switch (partnerEventType) {
		case API_KEY_APPROVED:
			partnerManager.handleApiKeyApproved(eventModel);
			break;
		case MISP_LIC_GENERATED:
		case MISP_LIC_UPDATED:
			partnerManager.updateMispLicenseData(eventModel);
			break;
		case PARTNER_UPDATED:
			partnerManager.updatePartnerData(eventModel);
			break;
		case PARTNER_API_KEY_UPDATED:
			partnerManager.handleApiKeyUpdated(eventModel);
			break;
		case POLICY_UPDATED:
			partnerManager.updatePolicyData(eventModel);
			break;
		default:
			break;
		}
	}

}
