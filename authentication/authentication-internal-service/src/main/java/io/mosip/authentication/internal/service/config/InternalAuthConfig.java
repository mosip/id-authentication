package io.mosip.authentication.internal.service.config;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.FMR_ENABLED_TEST;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.INTERNAL_ALLOWED_AUTH_TYPE;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import io.mosip.authentication.common.service.config.IdAuthConfig;
import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.idrepository.core.constant.IDAEventType;
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
	
	@Value("${"+ IdAuthConfigKeyConstants.IDA_WEBSUB_HUB_URL +"}")
	private String hubURL;
	
	@Value("${"+ IdAuthConfigKeyConstants.IDA_WEBSUB_CALLBACK_URL +"}")
	private String callbackURL;
	
	@Value("${"+ IdAuthConfigKeyConstants.IDA_WEBSUB_SECRET +"}")
	private String secret;
	
	@Autowired
	protected Environment environment;
	
	@Autowired
	SubscriptionClient<SubscriptionChangeRequest, UnsubscriptionRequest, SubscriptionChangeResponse> subscribe; 
	
	@PostConstruct
	public void init() {
		SubscriptionChangeRequest subscriptionRequest = new SubscriptionChangeRequest();
		subscriptionRequest.setCallbackURL(callbackURL);
		subscriptionRequest.setHubURL(hubURL);
		subscriptionRequest.setSecret(secret);
		subscriptionRequest.setTopic(IDAEventType.AUTH_TYPE_STATUS_UPDATE.name());
		subscribe.subscribe(subscriptionRequest);
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
