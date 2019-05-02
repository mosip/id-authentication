package io.mosip.registartion.processor.abis.middleware.stage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import io.mosip.registartion.processor.abis.middleware.processor.AbisMiddleWareProcessor;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;

public class AbisMiddleWareStage extends MosipVerticleManager {
	
	@Autowired
	private AbisMiddleWareProcessor abisMiddleWareProcessor;
	

	@Value("${vertx.cluster.configuration}")
	private String clusterManagerUrl;
	
	public void deployVerticle() {
		MosipEventBus mosipEventBus = this.getEventBus(this, clusterManagerUrl, 50);
		this.consumeAndSend(mosipEventBus, MessageBusAddress.ABIS_MIDDLEWARE_BUS_IN, MessageBusAddress.ABIS_MIDDLEWARE_BUS_OUT);
	}

	@Override
	public MessageDTO process(MessageDTO object) {
		return abisMiddleWareProcessor.process(object, this.getClass().getSimpleName());
	}

}
