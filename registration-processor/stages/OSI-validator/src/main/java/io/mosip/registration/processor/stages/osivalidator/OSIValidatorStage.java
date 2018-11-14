package io.mosip.registration.processor.stages.osivalidator;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

@RefreshScope
@Service
public class OSIValidatorStage extends MosipVerticleManager {

	/** The log. */
	private static Logger log = LoggerFactory.getLogger(OSIValidatorStage.class);

	@Value("${registration.processor.vertx.cluster.address}")
	private String clusterAddress;

	@Value("${registration.processor.vertx.localhost}")
	private String localhost;

	@Autowired
	OSIValidator osiValidator;

	/**
	 * Deploy verticle.
	 */
	public void deployVerticle() {
		MosipEventBus mosipEventBus = this.getEventBus(this.getClass(), clusterAddress, localhost);
		this.consumeAndSend(mosipEventBus, MessageBusAddress.OSI_BUS_IN, MessageBusAddress.OSI_BUS_OUT);
	}

	@Override
	public MessageDTO process(MessageDTO object) {
		object.setMessageBusAddress(MessageBusAddress.OSI_BUS_IN);
		object.setIsValid(Boolean.FALSE);
		object.setInternalError(Boolean.FALSE);

		String registrationId = object.getRid();
		boolean isValidOSI = false;

		try {
			isValidOSI = osiValidator.isValidOSI(registrationId);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (isValidOSI) {

		} else {

		}
		return object;
	}

}
