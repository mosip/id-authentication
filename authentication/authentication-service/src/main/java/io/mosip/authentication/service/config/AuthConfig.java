package io.mosip.authentication.service.config;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.ALLOWED_AUTH_TYPE;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.FMR_ENABLED_TEST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;

import io.mosip.authentication.common.service.config.IdAuthConfig;
import io.mosip.authentication.common.service.impl.match.BioAuthType;

@Configuration
@EnableCaching
@EnableAsync
public class AuthConfig extends IdAuthConfig {
	
	@Autowired
	protected Environment environment;

	protected boolean isFingerAuthEnabled() {
		return (environment.getProperty(ALLOWED_AUTH_TYPE).contains(BioAuthType.FGR_IMG.getConfigNameValue())
				|| (FMR_ENABLED_TEST.test(environment) && environment.getProperty(ALLOWED_AUTH_TYPE)
						.contains(BioAuthType.FGR_MIN.getConfigNameValue())));
	}
	
	protected boolean isFaceAuthEnabled() {
		return environment.getProperty(ALLOWED_AUTH_TYPE).contains(BioAuthType.FACE_IMG.getConfigNameValue());
	}
	
	protected boolean isIrisAuthEnabled() {
		return environment.getProperty(ALLOWED_AUTH_TYPE).contains(BioAuthType.IRIS_IMG.getConfigNameValue());
	}
}
