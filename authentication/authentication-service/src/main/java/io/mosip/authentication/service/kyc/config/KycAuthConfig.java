package io.mosip.authentication.service.kyc.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import io.mosip.authentication.common.service.config.IdAuthConfig;
import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.common.service.util.EnvUtil;

/**
 * The Class KycAuthConfig.
 *
 * @author Manoj SP
 */
@Configuration
@EnableCaching
@EnableAsync
public class KycAuthConfig extends IdAuthConfig {
	
	@Autowired
	protected EnvUtil environment;

	/* (non-Javadoc)
	 * @see io.mosip.authentication.common.service.config.IdAuthConfig#isFingerAuthEnabled()
	 */
	protected boolean isFingerAuthEnabled() {
		return (EnvUtil.getEkycAllowedAuthType().contains(BioAuthType.FGR_IMG.getConfigNameValue())
				|| (EnvUtil.getIsFmrEnabled() && EnvUtil.getEkycAllowedAuthType()
						.contains(BioAuthType.FGR_MIN.getConfigNameValue())));
	}
	
	/* (non-Javadoc)
	 * @see io.mosip.authentication.common.service.config.IdAuthConfig#isFaceAuthEnabled()
	 */
	protected boolean isFaceAuthEnabled() {
		return EnvUtil.getEkycAllowedAuthType().contains(BioAuthType.FACE_IMG.getConfigNameValue());
	}
	
	/* (non-Javadoc)
	 * @see io.mosip.authentication.common.service.config.IdAuthConfig#isIrisAuthEnabled()
	 */
	protected boolean isIrisAuthEnabled() {
		return EnvUtil.getEkycAllowedAuthType().contains(BioAuthType.IRIS_IMG.getConfigNameValue());
	}
}
