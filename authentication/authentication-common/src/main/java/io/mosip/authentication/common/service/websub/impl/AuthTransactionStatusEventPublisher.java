package io.mosip.authentication.common.service.websub.impl;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.AUTH_TRANSACTION_STATUS_TOPIC;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.helper.WebSubHelper;
import io.mosip.authentication.common.service.impl.idevent.AuthTransactionStatusEvent;
import io.mosip.authentication.common.service.websub.dto.EventModel;
import io.mosip.authentication.core.autntxn.dto.AutnTxnDto;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * The Class CredentialStoreStatusEventPublisher.
 * 
 * @author Loganathan Sekar
 */
@Component
public class AuthTransactionStatusEventPublisher extends BaseWebSubEventsInitializer {

	/** The Constant logger. */
	private static final Logger logger = IdaLogger.getLogger(AuthTransactionStatusEventPublisher.class);

	/** The credential status update topic. */
	@Value("${" + AUTH_TRANSACTION_STATUS_TOPIC + "}")
	private String authTransactionStatusTopic;
	
	@Autowired
	private ObjectMapper objectMapper;
	
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
			logger.debug(IdAuthCommonConstants.SESSION_ID, "tryRegisterTopicHotlistEvent", "",
					"Trying to register topic: " + authTransactionStatusTopic);
			webSubHelper.registerTopic(authTransactionStatusTopic);
			logger.info(IdAuthCommonConstants.SESSION_ID, "tryRegisterTopicHotlistEvent", "",
					"Registered topic: " + authTransactionStatusTopic);
		} catch (Exception e) {
			logger.info(IdAuthCommonConstants.SESSION_ID, "tryRegisterTopicHotlistEvent", e.getClass().toString(),
					"Error registering topic: " + authTransactionStatusTopic + "\n" + e.getMessage());
		}
	}


	@Override
	protected void doRegister() {
		logger.info(IdAuthCommonConstants.SESSION_ID, "doRegister", this.getClass().getSimpleName(),
				"Registering hotlist event topic..");
		tryRegisterTopic();
	}
	
	@SuppressWarnings("unchecked")
	public void publishEvent(AutnTxnDto authTxnDto, String transactionId, LocalDateTime updatedDTimes) {
		AuthTransactionStatusEvent credentialStatusUpdateEvent = createEvent(transactionId, updatedDTimes);
		EventModel<AuthTransactionStatusEvent> eventModel = webSubHelper.createEventModel(authTransactionStatusTopic, credentialStatusUpdateEvent);
		TypeReference<Map<String,Object>> typeReference = new TypeReference<Map<String, Object>>() {};
		eventModel.getEvent().setData((java.util.Map<String, Object>) objectMapper.convertValue(authTxnDto, typeReference));
		webSubHelper.publishEvent(authTransactionStatusTopic, eventModel);
	}
	
	/**
	 * Creates the credential status update event.
	 *
	 * @param requestId the request id
	 * @param status the status
	 * @param updatedTimestamp the updated timestamp
	 * @return the credential status update event
	 */
	private AuthTransactionStatusEvent createEvent(String transactionId,  LocalDateTime updatedTimestamp) {
		AuthTransactionStatusEvent event = new AuthTransactionStatusEvent();
		event.setTransactionId(transactionId);
		event.setTimestamp(updatedTimestamp);
		return event;
	}
	
}
