package io.mosip.registration.processor.biometric.authentication.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.mosip.registration.processor.biometric.authentication.stage.BiometricAuthenticationStage;

@Configuration
public class BiometricAuthConfigBean {
	@Bean
	public BiometricAuthenticationStage getBiometricAuthenticationStage() {
		return new BiometricAuthenticationStage();
	}

}
