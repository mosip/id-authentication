package io.mosip.authentication.internal.service.config;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.FMR_ENABLED_TEST;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.INTERNAL_ALLOWED_AUTH_TYPE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import io.mosip.authentication.common.service.config.IdAuthConfig;
import io.mosip.authentication.common.service.impl.match.BioAuthType;

/**
 * Class for defining configurations for the service.
 * 
 * @author Manoj SP
 *
 */
@Configuration
public class InternalAuthConfig extends IdAuthConfig {
	
	@Autowired
	protected Environment environment;
	

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
