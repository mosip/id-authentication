package io.mosip.authentication.service.config;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.ALLOWED_AUTH_TYPE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import io.mosip.authentication.common.service.config.IdAuthConfig;
import io.mosip.authentication.common.service.impl.match.BioAuthType;

@Configuration
public class AuthConfig extends IdAuthConfig {
	
	@Autowired
	protected Environment environment;

	protected boolean isFingerAuthEnabled() {
		return (environment.getProperty(ALLOWED_AUTH_TYPE).contains(BioAuthType.FGR_MIN.getConfigKey())
				|| environment.getProperty(ALLOWED_AUTH_TYPE).contains(BioAuthType.FGR_IMG.getConfigKey()));
	}
	
	protected boolean isFaceAuthEnabled() {
		return environment.getProperty(ALLOWED_AUTH_TYPE).contains(BioAuthType.FACE_IMG.getConfigKey());
	}
	
	protected boolean isIrisAuthEnabled() {
		return environment.getProperty(ALLOWED_AUTH_TYPE).contains(BioAuthType.IRIS_IMG.getConfigKey());
	}
}
