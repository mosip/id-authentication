package io.mosip.registration.processor.abis;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import io.mosip.registration.processor.abis.messagequeue.AbisMessageQueueImpl;
import io.mosip.registration.processor.core.exception.RegistrationProcessorCheckedException;

public class RestAbisApplication {

	public static void main(String[] args) throws RegistrationProcessorCheckedException {
		AnnotationConfigApplicationContext configApplicationContext = new AnnotationConfigApplicationContext();
		configApplicationContext.scan("io.mosip.registration.processor.abis.config",
				"io.mosip.registration.processor.stages.config", "io.mosip.registration.processor.status.config",
				"io.mosip.registration.processor.packet.storage.config", "io.mosip.registration.processor.core.config",
				"io.mosip.registration.processor.core.kernel.beans", "io.mosip.kernel.auth.*","io.mosip.registration.processor.packet.manager.config");
		configApplicationContext.refresh();
		AbisMessageQueueImpl abisMessageQueueImpl = configApplicationContext.getBean(AbisMessageQueueImpl.class);
		abisMessageQueueImpl.runAbisQueue();
	}
}
