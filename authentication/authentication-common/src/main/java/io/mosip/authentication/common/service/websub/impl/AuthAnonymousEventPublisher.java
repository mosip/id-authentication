package io.mosip.authentication.common.service.websub.impl;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.AUTH_ANONYMOUS_PROFILE_TOPIC;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.impl.idevent.AnonymousAuthenticationProfile;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.websub.model.EventModel;

/**
 * The Class AuthAnonymousEventPublisher.
 * 
 * @author Loganathan Sekar
 */
@Component
public class AuthAnonymousEventPublisher extends BaseWebSubEventsInitializer {

	/** The Constant logger. */
	private static final Logger logger = IdaLogger.getLogger(AuthAnonymousEventPublisher.class);

	/** The credential status update topic. */
	@Value("${" + AUTH_ANONYMOUS_PROFILE_TOPIC + "}")
	private String authAnanymousProfileTopic;
	
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
					"Trying to register topic: " + authAnanymousProfileTopic);
			webSubHelper.registerTopic(authAnanymousProfileTopic);
			logger.info(IdAuthCommonConstants.SESSION_ID, "tryRegisterTopicHotlistEvent", "",
					"Registered topic: " + authAnanymousProfileTopic);
		} catch (Exception e) {
			logger.info(IdAuthCommonConstants.SESSION_ID, "tryRegisterTopicHotlistEvent", e.getClass().toString(),
					"Error registering topic: " + authAnanymousProfileTopic + "\n" + e.getMessage());
		}
	}


	@Override
	protected void doRegister() {
		logger.info(IdAuthCommonConstants.SESSION_ID, "doRegister", this.getClass().getSimpleName(),
				"Registering hotlist event topic..");
		tryRegisterTopic();
	}
	
	@SuppressWarnings("unchecked")
	public void publishEvent(AnonymousAuthenticationProfile credentialStatusUpdateEvent) {
		EventModel eventModel = webSubHelper.createEventModel(authAnanymousProfileTopic);
		TypeReference<Map<String,Object>> typeReference = new TypeReference<Map<String, Object>>() {};
		eventModel.getEvent().setData((java.util.Map<String, Object>) objectMapper.convertValue(credentialStatusUpdateEvent, typeReference));
		webSubHelper.publishEvent(authAnanymousProfileTopic, eventModel);
	}
	
}
