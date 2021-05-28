package io.mosip.authentication.common.service.websub.impl;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.AUTH_FRAUD_ANALYSIS_TOPIC;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.helper.WebSubHelper;
import io.mosip.authentication.common.service.websub.dto.EventModel;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.dto.IdAuthFraudAnalysisEventDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.websub.model.Event;

/**
 * The Class CredentialStoreStatusEventPublisher.
 * 
 * @author Loganathan Sekar
 */
@Component
public class IdAuthFraudAnalysisEventPublisher extends BaseWebSubEventsInitializer {

	/** The Constant logger. */
	private static final Logger logger = IdaLogger.getLogger(IdAuthFraudAnalysisEventPublisher.class);

	/** The credential status update topic. */
	@Value("${" + AUTH_FRAUD_ANALYSIS_TOPIC + "}")
	private String fraudAnalysisTopic;
	
	/** The web sub event publish helper. */
	@Autowired
	private WebSubHelper webSubHelper;

	@Autowired
	private ObjectMapper objectMapper;
	
	/**
	 * Do subscribe.
	 */
	@Override
	protected void doSubscribe() {
		//Nothing to do here since we are just publishing event for this topic
	}

	@Override
	protected void doRegister() {
		try {
			logger.info(IdAuthCommonConstants.SESSION_ID, "doRegister", this.getClass().getSimpleName(),
					"Registering fraud management event topic..");
			publisher.registerTopic(fraudAnalysisTopic, publisherUrl);
			logger.info(IdAuthCommonConstants.SESSION_ID, "tryRegisterTopic", "", "Registered topic: " + fraudAnalysisTopic);
		} catch (Exception e) {
			logger.info(IdAuthCommonConstants.SESSION_ID, "tryRegisterTopic", e.getClass().toString(),
					"Error registering topic: " + fraudAnalysisTopic + "\n" + e.getMessage());
		}
	}
	
	@SuppressWarnings("unchecked")
	public void publishEvent(IdAuthFraudAnalysisEventDTO eventData) {
		Event event = new Event();
		EventModel<Object> eventModel = webSubHelper.createEventModel(fraudAnalysisTopic, event);
		eventModel.setData(objectMapper.convertValue(eventData, Map.class));
		webSubHelper.publishEvent(fraudAnalysisTopic, eventModel);
	}
}
