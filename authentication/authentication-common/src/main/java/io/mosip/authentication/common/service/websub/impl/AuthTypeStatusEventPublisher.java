package io.mosip.authentication.common.service.websub.impl;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.AUTH_TYPE_STATUS_ACK_TOPIC;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.impl.idevent.AuthTypeStatusUpdateAckEvent;
import io.mosip.authentication.common.service.websub.dto.EventModel;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * The Class CredentialStoreStatusEventPublisher.
 * 
 * @author Loganathan Sekar
 */
@Component
public class AuthTypeStatusEventPublisher extends BaseWebSubEventsInitializer {

	private static final String TRY_REGISTER_TOPIC_HOTLIST_EVENT = "tryRegisterTopicHotlistEvent";

	/** The Constant logger. */
	private static final Logger logger = IdaLogger.getLogger(AuthTypeStatusEventPublisher.class);

	/** The credential status update topic. */
	@Value("${" + AUTH_TYPE_STATUS_ACK_TOPIC + "}")
	private String authTypeStatusAcknlowedgeTopic;
	

	/**
	 * Do subscribe.
	 */
	@Override
	protected void doSubscribe() {
		//Nothing to do here since we are just publishing event for this topic
	}

	/**
	 * Try register topic partner service events.
	 */
	private void tryRegisterTopic() {
		try {
			logger.debug(IdAuthCommonConstants.SESSION_ID, TRY_REGISTER_TOPIC_HOTLIST_EVENT, "",
					"Trying to register topic: " + getTopic());
			webSubHelper.registerTopic(getTopic());
			logger.info(IdAuthCommonConstants.SESSION_ID, TRY_REGISTER_TOPIC_HOTLIST_EVENT, "",
					"Registered topic: " + getTopic());
		} catch (Exception e) {
			logger.info(IdAuthCommonConstants.SESSION_ID, TRY_REGISTER_TOPIC_HOTLIST_EVENT, e.getClass().toString(),
					"Error registering topic: " + getTopic() + "\n" + e.getMessage());
		}
	}


	@Override
	protected void doRegister() {
		logger.info(IdAuthCommonConstants.SESSION_ID, "doRegister", this.getClass().getSimpleName(),
				"Registering hotlist event topic..");
		tryRegisterTopic();
	}
	
	public void publishEvent(String status, String requestId, LocalDateTime updatedDTimes) {
		AuthTypeStatusUpdateAckEvent credentialStatusUpdateEvent = createEvent(requestId, status, updatedDTimes);
		EventModel<AuthTypeStatusUpdateAckEvent> eventModel = webSubHelper.createEventModel(getTopic(), credentialStatusUpdateEvent);
		webSubHelper.publishEvent(getTopic(), eventModel);
	}
	
	/**
	 * Creates the credential status update event.
	 *
	 * @param requestId the request id
	 * @param status the status
	 * @param updatedTimestamp the updated timestamp
	 * @return the credential status update event
	 */
	private AuthTypeStatusUpdateAckEvent createEvent(String requestId, String status, LocalDateTime updatedTimestamp) {
		AuthTypeStatusUpdateAckEvent event = new AuthTypeStatusUpdateAckEvent();
		event.setStatus(status);
		event.setRequestId(requestId);
		event.setTimestamp(updatedTimestamp);
		return event;
	}

	/**
	 * @return the authTypeStatusAcknlowedgeTopic
	 */
	public String getTopic() {
		return authTypeStatusAcknlowedgeTopic;
	}
	
}
