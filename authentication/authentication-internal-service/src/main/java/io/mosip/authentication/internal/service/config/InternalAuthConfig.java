package io.mosip.authentication.internal.service.config;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.FMR_ENABLED_TEST;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.INTERNAL_ALLOWED_AUTH_TYPE;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;

import io.mosip.authentication.common.service.config.IdAuthConfig;
import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.idrepository.core.constant.IDAEventType;
import io.mosip.idrepository.core.dto.EventModel;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.websub.spi.PublisherClient;
import io.mosip.kernel.core.websub.spi.SubscriptionClient;
import io.mosip.kernel.websub.api.model.SubscriptionChangeRequest;
import io.mosip.kernel.websub.api.model.SubscriptionChangeResponse;
import io.mosip.kernel.websub.api.model.UnsubscriptionRequest;

/**
 * Class for defining configurations for the service.
 * 
 * @author Manoj SP
 *
 */
@Configuration
public class InternalAuthConfig extends IdAuthConfig {
	
	private static Logger logger = IdaLogger.getLogger(InternalAuthConfig.class);
	
	@Value("${"+ IdAuthConfigKeyConstants.IDA_WEBSUB_HUB_URL +"}")
	private String hubURL;
	
	@Value("${"+ IdAuthConfigKeyConstants.IDA_WEBSUB_PUBLISHER_URL +"}")
	private String publisherUrl;
	
	@Value("${"+ IdAuthConfigKeyConstants.IDA_WEBSUB_AUTH_TYPE_CALLBACK_URL +"}")
	private String authTypeCallbackURL;
	
	@Value("${"+ IdAuthConfigKeyConstants.IDA_WEBSUB_CREDENTIAL_ISSUE_CALLBACK_URL +"}")
	private String credentialIssueCallbackURL;
	
	@Value("${"+ IdAuthConfigKeyConstants.IDA_WEBSUB_SECRET +"}")
	private String secret;
	
	@Autowired
	private PublisherClient<String, EventModel, HttpHeaders> publisher; 
	
	@Autowired
	protected Environment environment;
	
	@Autowired
	SubscriptionClient<SubscriptionChangeRequest, UnsubscriptionRequest, SubscriptionChangeResponse> subscribe; 
	
	@PostConstruct
	public void init() {
		tryRegisterTopicForAuthEvents();
		subscribeForAuthTypeEvents();
	}

	private void tryRegisterTopicForAuthEvents() {
		String topic = IDAEventType.AUTH_TYPE_STATUS_UPDATE.name();
		try {
			publisher.registerTopic(topic, publisherUrl);	
		} catch (Exception e) {
			logger.info(IdAuthCommonConstants.SESSION_ID, "tryRegisterTopicForAuthEvents",  e.getClass().toString(), "Error subscribing topic: "+ topic +"\n" + e.getMessage());
		}
	}

	private void subscribeForAuthTypeEvents() {
		String topic = IDAEventType.AUTH_TYPE_STATUS_UPDATE.name();
		try {
			SubscriptionChangeRequest subscriptionRequest = new SubscriptionChangeRequest();
			subscriptionRequest.setCallbackURL(authTypeCallbackURL);
			subscriptionRequest.setHubURL(hubURL);
			subscriptionRequest.setSecret(secret);
			subscriptionRequest.setTopic(topic);
			logger.info(IdAuthCommonConstants.SESSION_ID, "subscribeForAuthTypeEvents", "", "Trying to register topic: " + topic);
			subscribe.subscribe(subscriptionRequest);
		} catch (Exception e) {
			logger.info(IdAuthCommonConstants.SESSION_ID, "subscribeForAuthTypeEvents",  e.getClass().toString(), "Error subscribing topic: "+ topic +"\n" + e.getMessage());
			throw e;
		}
	}


	/* (non-Javadoc)
	 * @see io.mosip.authentication.common.service.config.IdAuthConfig#isFingerAuthEnabled()
	 */
	protected boolean isFingerAuthEnabled() {
		return (environment.getProperty(INTERNAL_ALLOWED_AUTH_TYPE).contains(BioAuthType.FGR_IMG.getConfigNameValue())
				|| (FMR_ENABLED_TEST.test(environment) && environment.getProperty(INTERNAL_ALLOWED_AUTH_TYPE)
						.contains(BioAuthType.FGR_MIN.getConfigNameValue())));
	}
	
	/* (non-Javadoc)
	 * @see io.mosip.authentication.common.service.config.IdAuthConfig#isFaceAuthEnabled()
	 */
	protected boolean isFaceAuthEnabled() {
		return environment.getProperty(INTERNAL_ALLOWED_AUTH_TYPE).contains(BioAuthType.FACE_IMG.getConfigNameValue());
	}
	
	/* (non-Javadoc)
	 * @see io.mosip.authentication.common.service.config.IdAuthConfig#isIrisAuthEnabled()
	 */
	protected boolean isIrisAuthEnabled() {
		return environment.getProperty(INTERNAL_ALLOWED_AUTH_TYPE).contains(BioAuthType.IRIS_IMG.getConfigNameValue());
	}

}
