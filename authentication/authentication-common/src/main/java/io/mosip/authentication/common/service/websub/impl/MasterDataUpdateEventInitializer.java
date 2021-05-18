package io.mosip.authentication.common.service.websub.impl;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_MASTERDATA_TEMPLATES_CALLBACK_SECRET;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_MASTERDATA_TEMPLATES_CALLBACK_URL;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_MASTERDATA_TEMPLATES_TOPIC;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_MASTERDATA_TITLES_CALLBACK_SECRET;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_MASTERDATA_TITLES_CALLBACK_URL;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_MASTERDATA_TITLES_TOPIC;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * The Class MasterDataUpdateEventInitializer.
 * 
 * @author Loganathan Sekar
 */
@Component
public class MasterDataUpdateEventInitializer extends BaseWebSubEventsInitializer {

	/** The Constant logger. */
	private static final Logger logger = IdaLogger.getLogger(MasterDataUpdateEventInitializer.class);

	@Value("${" + IDA_WEBSUB_MASTERDATA_TEMPLATES_CALLBACK_URL + "}")
	private String masterdataTemplatesCallbackURL;

	@Value("${" + IDA_WEBSUB_MASTERDATA_TEMPLATES_CALLBACK_SECRET + "}")
	private String masterdataTemplatesCallbackSecret;

	@Value("${" + IDA_WEBSUB_MASTERDATA_TEMPLATES_TOPIC + "}")
	private String masterdataTemplatesEventTopic;
	
	@Value("${" + IDA_WEBSUB_MASTERDATA_TITLES_CALLBACK_URL + "}")
	private String masterdataTitlesCallbackURL;

	@Value("${" + IDA_WEBSUB_MASTERDATA_TITLES_CALLBACK_SECRET + "}")
	private String masterdataTitlesCallbackSecret;

	@Value("${" + IDA_WEBSUB_MASTERDATA_TITLES_TOPIC + "}")
	private String masterdataTitlesEventTopic;

	/**
	 * Do subscribe.
	 */
	@Override
	protected void doSubscribe() {
		logger.info(IdAuthCommonConstants.SESSION_ID, "doSubscribe", this.getClass().getSimpleName(),
				"Initializing hotlist event subscriptions..");
		subscribeForEvent(masterdataTemplatesEventTopic, masterdataTemplatesCallbackURL, masterdataTemplatesCallbackSecret);
		subscribeForEvent(masterdataTitlesEventTopic, masterdataTitlesCallbackURL, masterdataTitlesCallbackSecret);
	}

	@Override
	protected void doRegister() {
		logger.info(IdAuthCommonConstants.SESSION_ID, "doRegister", this.getClass().getSimpleName(),
				"Registering hotlist event topic..");
		tryRegisterTopicEvent(masterdataTemplatesEventTopic);
		tryRegisterTopicEvent(masterdataTitlesEventTopic);
	}
}
