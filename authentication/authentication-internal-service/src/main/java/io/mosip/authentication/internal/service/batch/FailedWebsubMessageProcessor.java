package io.mosip.authentication.internal.service.batch;

import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.helper.WebSubHelper.FailedMessage;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.PartnerEventTypes;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.idrepository.core.constant.IDAEventType;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * The Class FailedWebsubMessageProcessor.
 * @author Loganathan Sekar
 */
@Component
public class FailedWebsubMessageProcessor {
	
	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(FailedWebsubMessageProcessor.class);
	
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
	 */
	private void doProcessFailedMessage(FailedMessage failedMessage) {
		failedMessage.getFailedMessageConsumer().accept(failedMessage);
	}

	public void processIdChangeEvent(IDAEventType eventType, FailedMessage failedMessage) {
		
	}

	public void processAuthTypeStatusEvent(FailedMessage failedMessage) {
		
	}
	public void processHotlistEvent(FailedMessage failedMessage) {
		
	}

	public void processMasterdataTemplatesEvent(FailedMessage failedMessage) {
		
	}

	public void processMasterdataTitlesEvent(FailedMessage failedMessage) {
		
	}

	public void processPartnerCertEvent(FailedMessage failedMessage) {
		
	}

	public void processPartnerEvent(PartnerEventTypes partnerEventType, FailedMessage failedMessage) {

	}

}
