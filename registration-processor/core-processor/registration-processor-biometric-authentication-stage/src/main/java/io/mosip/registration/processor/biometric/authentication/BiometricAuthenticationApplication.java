package io.mosip.registration.processor.biometric.authentication;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import io.mosip.registration.processor.biometric.authentication.stage.BiometricAuthenticationStage;

public class BiometricAuthenticationApplication {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.scan("io.mosip.registration.processor.biometric.authentication.config",
				"io.mosip.registration.processor.status.config", "io.mosip.registration.processor.rest.client.config",
				"io.mosip.registration.processor.packet.storage.config", "io.mosip.registration.processor.core.config",
				"io.mosip.registration.processor.core.kernel.beans",
				"io.mosip.registration.processor.packet.manager.config");
		ctx.refresh();
		BiometricAuthenticationStage validatebean = (BiometricAuthenticationStage) ctx
				.getBean(BiometricAuthenticationStage.class);
		validatebean.deployVerticle();
	}

}
