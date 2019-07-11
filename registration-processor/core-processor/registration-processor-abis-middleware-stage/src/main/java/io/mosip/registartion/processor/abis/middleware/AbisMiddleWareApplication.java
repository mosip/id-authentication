package io.mosip.registartion.processor.abis.middleware;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import io.mosip.registartion.processor.abis.middleware.stage.AbisMiddleWareStage;
import io.mosip.registration.processor.core.exception.RegistrationProcessorCheckedException;

/**
 * @author Girish Yarru
 * @since v1.0
 *
 */
public class AbisMiddleWareApplication {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext configApplicationContext = new AnnotationConfigApplicationContext();
		configApplicationContext.scan("io.mosip.registration.processor.abis.handler.config",
                "io.mosip.registration.processor.status.config",
                "io.mosip.registration.processor.rest.client.config",
                "io.mosip.registration.processor.packet.storage.config",
                "io.mosip.registration.processor.core.config",
                "io.mosip.registration.processor.core.kernel.beans",
                "io.mosip.registration.processor.packet.manager.config",
                "io.mosip.registration.processor.stages.config");
		configApplicationContext.refresh();
		AbisMiddleWareStage demodedupeStage = configApplicationContext.getBean(AbisMiddleWareStage.class);
	
			demodedupeStage.deployVerticle();


	}
}
