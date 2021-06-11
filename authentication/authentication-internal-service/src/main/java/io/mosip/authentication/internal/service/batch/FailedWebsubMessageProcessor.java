package io.mosip.authentication.internal.service.batch;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_AUTH_PARTNER_ID;

import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.helper.WebSubHelper.FailedMessage;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.PartnerEventTypes;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
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
 * @author Loganathan Sekar
 */
@Component
public class FailedWebsubMessageProcessor {
	
	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(FailedWebsubMessageProcessor.class);
	
	@Value("${"+ IDA_AUTH_PARTNER_ID  +"}")
	private String authPartherId;
	
	@Autowired
	private IdChangeEventHandlerService credentialStoreService;
	
	@Autowired
	private UpdateAuthtypeStatusService authtypeStatusService;
	
	@Autowired
	private HotlistService hotlistService;
	
	@Autowired
	private MasterDataCacheUpdateService masterDataCacheUpdateService;
	
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
	 * @throws Exception 
	 */
	private void doProcessFailedMessage(FailedMessage failedMessage) throws Exception {
		failedMessage.getFailedMessageConsumer().accept(failedMessage);
	}

	public void processIdChangeEvent(IDAEventType eventType, FailedMessage failedMessage) throws IdAuthenticationBusinessException {
		mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, "processIdChangeEvent", "", "handling " + eventType + " event for partnerId: " + authPartherId);
		EventModel event = mapper.convertValue(failedMessage.getMessage(), EventModel.class);
		credentialStoreService.handleIdEvent(event);
	}

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
	
	public void processHotlistEvent(FailedMessage failedMessage) throws IdAuthenticationBusinessException {
		mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, this.getClass().getCanonicalName(), "processHotlistEvent", "EVENT RECEIVED");

		EventModel eventModel = mapper.convertValue(failedMessage.getMessage(), EventModel.class);
		hotlistService.handlingHotlistingEvent(eventModel);
	}

	public void processMasterdataTemplatesEvent(FailedMessage failedMessage) {
		mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, this.getClass().getCanonicalName(), "processMasterdataTemplatesEvent", "EVENT RECEIVED");
		EventModel eventModel = mapper.convertValue(failedMessage.getMessage(), EventModel.class);
		masterDataCacheUpdateService.updateTemplates(eventModel);
	}

	public void processMasterdataTitlesEvent(FailedMessage failedMessage) {
		mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, this.getClass().getCanonicalName(), "processMasterdataTitlesEvent", "EVENT RECEIVED");
		EventModel eventModel = mapper.convertValue(failedMessage.getMessage(), EventModel.class);
		masterDataCacheUpdateService.updateTemplates(eventModel);
	}

	public void processPartnerCACertEvent(FailedMessage failedMessage) {
		
	}

	public void processPartnerEvent(PartnerEventTypes partnerEventType, FailedMessage failedMessage) {

	}

}
