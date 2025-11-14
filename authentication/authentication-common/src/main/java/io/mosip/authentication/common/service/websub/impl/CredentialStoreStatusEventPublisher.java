package io.mosip.authentication.common.service.websub.impl;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.CREDENTIAL_STATUS_UPDATE_TOPIC;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.helper.WebSubHelper;
import io.mosip.authentication.common.service.impl.idevent.CredentialStatusUpdateEvent;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;

/**
 * The Class CredentialStoreStatusEventPublisher.
 * 
 * @author Loganathan Sekar
 */
@Component
public class CredentialStoreStatusEventPublisher extends BaseWebSubEventsInitializer {

	/** The Constant logger. */
	private static final Logger logger = IdaLogger.getLogger(CredentialStoreStatusEventPublisher.class);

	/** The credential status update topic. */
	@Value("${" + CREDENTIAL_STATUS_UPDATE_TOPIC + "}")
	private String credentialStatusUpdateTopic;
	
	/** The web sub event publish helper. */
	@Autowired
	private WebSubHelper webSubHelper;

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
	private void tryRegisterTopicHotlistEvent() {
		try {
			logger.debug(IdAuthCommonConstants.SESSION_ID, "tryRegisterTopicHotlistEvent", "",
					"Trying to register topic: " + credentialStatusUpdateTopic);
			webSubHelper.registerTopic(credentialStatusUpdateTopic);
			logger.info(IdAuthCommonConstants.SESSION_ID, "tryRegisterTopicHotlistEvent", "",
					"Registered topic: " + credentialStatusUpdateTopic);
		} catch (Exception e) {
			logger.info(IdAuthCommonConstants.SESSION_ID, "tryRegisterTopicHotlistEvent", e.getClass().toString(),
					"Error registering topic: " + credentialStatusUpdateTopic + "\n" + e.getMessage());
		}
	}


	@Override
	protected void doRegister() {
		logger.info(IdAuthCommonConstants.SESSION_ID, "doRegister", this.getClass().getSimpleName(),
				"Registering hotlist event topic..");
		tryRegisterTopicHotlistEvent();
	}
	
	public void publishEvent(String status, String requestId, LocalDateTime updatedDTimes) {
		CredentialStatusUpdateEvent credentialStatusUpdateEvent = createCredentialStatusUpdateEvent(requestId, status, updatedDTimes);
		webSubHelper.publishEvent(credentialStatusUpdateTopic, webSubHelper.createEventModel(credentialStatusUpdateTopic, credentialStatusUpdateEvent));
	}
	
	/**
	 * Creates the credential status update event.
	 *
	 * @param requestId the request id
	 * @param status the status
	 * @param updatedTimestamp the updated timestamp
	 * @return the credential status update event
	 */
	private CredentialStatusUpdateEvent createCredentialStatusUpdateEvent(String requestId, String status, LocalDateTime updatedTimestamp) {
		CredentialStatusUpdateEvent credentialStatusUpdateEvent = new CredentialStatusUpdateEvent();
		credentialStatusUpdateEvent.setStatus(status);
		credentialStatusUpdateEvent.setRequestId(requestId);
		credentialStatusUpdateEvent.setTimestamp(DateUtils.formatToISOString(updatedTimestamp));
		return credentialStatusUpdateEvent;
	}
	
}
