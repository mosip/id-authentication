package io.mosip.registrationprocessor.stages.demodedupe;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;

@RefreshScope
@Service
public class DemodedupeStage extends MosipVerticleManager {
	
	@Value("${registration.processor.vertx.cluster.address}")
	private String clusterAddress;

	@Value("${registration.processor.vertx.localhost}")
	private String localhost;
	
	public void deployVerticle() {
		MosipEventBus mosipEventBus = this.getEventBus(this.getClass(), clusterAddress, localhost);
		this.consumeAndSend(mosipEventBus, MessageBusAddress.DEMODEDUPE_BUS_IN, MessageBusAddress.DEMODEDUPE_BUS_IN);
	}

	@Override
	public MessageDTO process(MessageDTO object) {
		object.setMessageBusAddress(MessageBusAddress.DEMODEDUPE_BUS_IN);
		object.setIsValid(Boolean.FALSE);
		object.setInternalError(Boolean.FALSE);
		String description = "";
		boolean isTransactionSuccessful = false;

		String registrationId = object.getRid();
		return null;
	}

}