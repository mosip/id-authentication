package io.mosip.authentication.kyc.service.config;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.EKYC_ALLOWED_AUTH_TYPE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import io.mosip.authentication.common.service.config.IdAuthConfig;
import io.mosip.authentication.common.service.impl.match.BioAuthType;

@Configuration
public class KycAuthConfig extends IdAuthConfig {
	
	@Autowired
	protected Environment environment;

	protected boolean isFingerAuthEnabled() {
		return (environment.getProperty(EKYC_ALLOWED_AUTH_TYPE).contains(BioAuthType.FGR_MIN.getConfigKey())
				|| environment.getProperty(EKYC_ALLOWED_AUTH_TYPE).contains(BioAuthType.FGR_IMG.getConfigKey()));
	}
	
	protected boolean isFaceAuthEnabled() {
		return environment.getProperty(EKYC_ALLOWED_AUTH_TYPE).contains(BioAuthType.FACE_IMG.getConfigKey());
	}
	
	protected boolean isIrisAuthEnabled() {
		return environment.getProperty(EKYC_ALLOWED_AUTH_TYPE).contains(BioAuthType.IRIS_IMG.getConfigKey());
	}
}
