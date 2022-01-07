package io.mosip.authentication.internal.service.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.context.SecurityContextHolder;

import io.mosip.authentication.common.service.config.IdAuthConfig;
import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.common.service.util.EnvUtil;

/**
 * Class for defining configurations for the service.
 * 
 * @author Manoj SP
 *
 */
@Configuration
@EnableCaching
@EnableAsync
public class InternalAuthConfig extends IdAuthConfig {

	@Autowired
	protected EnvUtil environment;
	
	@PostConstruct
	public void initialize() {
		SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.common.service.config.IdAuthConfig#
	 * isFingerAuthEnabled()
	 */
	protected boolean isFingerAuthEnabled() {
		return (EnvUtil.getInternalAuthAllowedType().contains(BioAuthType.FGR_IMG.getConfigNameValue())
				|| (EnvUtil.getIsFmrEnabled() && EnvUtil.getInternalAuthAllowedType()
						.contains(BioAuthType.FGR_MIN.getConfigNameValue())));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.common.service.config.IdAuthConfig#isFaceAuthEnabled(
	 * )
	 */
	protected boolean isFaceAuthEnabled() {
		return EnvUtil.getInternalAuthAllowedType().contains(BioAuthType.FACE_IMG.getConfigNameValue());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.common.service.config.IdAuthConfig#isIrisAuthEnabled(
	 * )
	 */
	protected boolean isIrisAuthEnabled() {
		return EnvUtil.getInternalAuthAllowedType().contains(BioAuthType.IRIS_IMG.getConfigNameValue());
	}

}
