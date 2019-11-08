package io.mosip.registration.processor.biometric.authentication.config;

import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.SecretKey;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import io.mosip.kernel.core.crypto.spi.CryptoCoreSpec;
import io.mosip.kernel.crypto.jce.core.CryptoCore;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;
import io.mosip.registration.processor.biometric.authentication.stage.BiometricAuthenticationStage;
import io.mosip.registration.processor.packet.storage.utils.AuthUtil;

@Configuration
public class BiometricAuthConfigBean {
	@Bean
	public BiometricAuthenticationStage getBiometricAuthenticationStage() {
		return new BiometricAuthenticationStage();
	}

	@Bean
	public AuthUtil getAuthUtil() {
		return new AuthUtil();
	}

	@Bean
	public KeyGenerator getKeyGenerator() {
		return new KeyGenerator();
	}

	@Bean
	@Primary
	public CryptoCoreSpec<byte[], byte[], SecretKey, PublicKey, PrivateKey, String> getEncryptor() {
		return new CryptoCore();
	}

}
