package io.mosip.authentication.common.service.websub.impl;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.REMOVE_ID_STATUS_TOPIC;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.helper.WebSubHelper;
import io.mosip.authentication.common.service.impl.idevent.RemoveIdStatusEvent;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils2;

/**
 * The Class RemoveIdStatusEventPublisher.
 * 
 * @author Ritik Jain
 */
@Component
public class RemoveIdStatusEventPublisher extends BaseWebSubEventsInitializer {

	/** The Constant logger. */
	private static final Logger logger = IdaLogger.getLogger(RemoveIdStatusEventPublisher.class);

	/** The remove id status topic. */
	@Value("${" + REMOVE_ID_STATUS_TOPIC + "}")
	private String removeIdStatusTopic;

	/** The web sub event publish helper. */
	@Autowired
	private WebSubHelper webSubHelper;

	private static final String ID_HASH = "id_hash";

	/**
	 * Do subscribe.
	 */
	@Override
	protected void doSubscribe() {
		// Nothing to do here since we are just publishing event for this topic.
	}

	/**
	 * Try register topic remove id status event.
	 */
	private void tryRegisterTopic() {
		try {
			logger.debug(IdAuthCommonConstants.SESSION_ID, "tryRegisterTopic", "",
					"Trying to register topic: " + removeIdStatusTopic);
			webSubHelper.registerTopic(removeIdStatusTopic);
			logger.info(IdAuthCommonConstants.SESSION_ID, "tryRegisterTopic", "",
					"Registered topic: " + removeIdStatusTopic);
		} catch (Exception e) {
			logger.info(IdAuthCommonConstants.SESSION_ID, "tryRegisterTopic", e.getClass().toString(),
					"Error registering topic: " + removeIdStatusTopic + "\n" + e.getMessage());
		}
	}

	@Override
	protected void doRegister() {
		logger.info(IdAuthCommonConstants.SESSION_ID, "doRegister", this.getClass().getSimpleName(),
				"Registering topic..");
		tryRegisterTopic();
	}

	public void publishRemoveIdStatusEvent(String idHash) {
		RemoveIdStatusEvent removeIdStatusEvent = createRemoveIdStatusEvent(idHash);
		webSubHelper.publishEvent(removeIdStatusTopic,
				webSubHelper.createEventModel(removeIdStatusTopic, removeIdStatusEvent));
	}

	/**
	 * Creates the remove id status event.
	 *
	 * @param idHash the idHash
	 * @return the remove id status event
	 */
	private RemoveIdStatusEvent createRemoveIdStatusEvent(String idHash) {
		RemoveIdStatusEvent removeIdStatusEvent = new RemoveIdStatusEvent();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put(ID_HASH, idHash);
		removeIdStatusEvent.setData(dataMap);
		removeIdStatusEvent.setTimestamp(DateUtils2.formatToISOString(DateUtils2.getUTCCurrentDateTime()));
		return removeIdStatusEvent;
	}

}
