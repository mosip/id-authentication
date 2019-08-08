package io.mosip.authentication.otp.service.config;

import org.springframework.context.annotation.Configuration;

import io.mosip.authentication.common.service.config.IdAuthConfig;

@Configuration
public class OtpAuthConfig extends IdAuthConfig {
	
	protected boolean isFingerAuthEnabled() {
		return false;
	}
	
	protected boolean isFaceAuthEnabled() {
		return false;
	}
	
	protected boolean isIrisAuthEnabled() {
		return false;
	}
}
