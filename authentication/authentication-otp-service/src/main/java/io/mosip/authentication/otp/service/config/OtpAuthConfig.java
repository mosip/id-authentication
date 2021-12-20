package io.mosip.authentication.otp.service.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import com.fasterxml.jackson.module.afterburner.AfterburnerModule;

import io.mosip.authentication.common.service.config.IdAuthConfig;

@Configuration
@EnableCaching
@EnableAsync
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
	
	@Bean
	public AfterburnerModule afterburnerModule() {
	  return new AfterburnerModule();
	}
}
