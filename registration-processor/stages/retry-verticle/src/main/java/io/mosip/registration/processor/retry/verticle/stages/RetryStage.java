package io.mosip.registration.processor.retry.verticle.stages;

import org.springframework.stereotype.Component;

import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;

@Component
public class RetryStage extends MosipVerticleManager {
	//TODO - Add class and method level comments
	public void deployVerticle() {
		MosipEventBus mosipEventBus = this.getEventBus(this.getClass());
		this.consume(mosipEventBus, MessageBusAddress.RETRY_BUS);

	}

	@Override
	public MessageDTO process(MessageDTO dto) {
		int retrycount = (dto.getRetryCount()==null)?0:dto.getRetryCount()+1;
		dto.setRetryCount(retrycount);
		//TODO - This threshold for retry needs be read from properties file
		if(dto.getRetryCount()<5) {
			this.send(this.getEventBus(this.getClass()), dto.getAddress(), dto);
		}
		else {
			this.send(this.getEventBus(this.getClass()), MessageBusAddress.ERROR, dto);
		}
		return null;
	}

}
