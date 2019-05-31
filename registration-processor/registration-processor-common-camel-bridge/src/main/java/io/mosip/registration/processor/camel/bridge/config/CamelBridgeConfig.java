package io.mosip.registration.processor.camel.bridge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import io.mosip.registration.processor.camel.bridge.MosipBridgeFactory;
import io.mosip.registration.processor.camel.bridge.processor.TokenGenerationProcessor;

@Configuration
@EnableAspectJAutoProxy
public class CamelBridgeConfig {
	
	@Bean
	public MosipBridgeFactory getMosipBridgeFactory() {
		return new MosipBridgeFactory();
	}
	
	@Bean
	public TokenGenerationProcessor tokenGenerationProcessor() {
		return new TokenGenerationProcessor();
	}

}