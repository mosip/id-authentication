package io.mosip.registartion.processor.abis.middleware;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import io.mosip.registartion.processor.abis.middleware.stage.AbisMiddleWareStage;
import io.mosip.registration.processor.core.exception.RegistrationProcessorCheckedException;
import io.mosip.registration.processor.core.exception.RegistrationProcessorUnCheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

/**
 * Hello world!
 *
 */
public class AbisMiddleWareApplication {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext configApplicationContext = new AnnotationConfigApplicationContext();
		configApplicationContext.scan("io.mosip.registration.processor.stages.config",
				"io.mosip.registration.processor.demo.dedupe.config", "io.mosip.registration.processor.status.config",
				"io.mosip.registration.processor.packet.storage.config", "io.mosip.registration.processor.core.config",
				"io.mosip.registration.processor.core.kernel.beans");
		configApplicationContext.refresh();
		AbisMiddleWareStage demodedupeStage = configApplicationContext.getBean(AbisMiddleWareStage.class);
		try {
			demodedupeStage.deployVerticle();
		} catch (RegistrationProcessorCheckedException e) {
			throw new RegistrationProcessorUnCheckedException(PlatformErrorMessages.UNKNOWN_EXCEPTION_OCCURED.getCode(),
					"Unknown exception occured while deploying", e);
		}

	}
}
