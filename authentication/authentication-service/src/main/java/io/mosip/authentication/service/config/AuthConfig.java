package io.mosip.authentication.service.config;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import io.mosip.authentication.common.service.config.IdAuthConfig;
import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.common.service.util.EnvUtil;

@Configuration
@EnableCaching
@EnableAsync
public class AuthConfig extends IdAuthConfig {
	
	protected boolean isFingerAuthEnabled() {
		return (EnvUtil.getAllowedAuthType().contains(BioAuthType.FGR_IMG.getConfigNameValue())
				|| (EnvUtil.getIsFmrEnabled() && EnvUtil.getAllowedAuthType()
						.contains(BioAuthType.FGR_MIN.getConfigNameValue())));
	}
	
	protected boolean isFaceAuthEnabled() {
		return EnvUtil.getAllowedAuthType().contains(BioAuthType.FACE_IMG.getConfigNameValue());
	}
	
	protected boolean isIrisAuthEnabled() {
		return EnvUtil.getAllowedAuthType().contains(BioAuthType.IRIS_IMG.getConfigNameValue());
	}
}
