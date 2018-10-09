package io.mosip.registration.processor.retry.verticle.stages;

import org.springframework.stereotype.Component;

import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;
@Component
public class RetryStage extends MosipVerticleManager {
	private static final String STRUCTURE_VALIDATION = "structure-bus-out";
	
	private static final String DEMOGRAPHIC_VALIDATION = "demographic-bus-out";
	
	private static final String BIOMETRIC_VALIDATION = "biometric-bus-out";
	
	
	
	public void deployVerticle() {
		 RetryStage retryStage = new RetryStage();
		 MosipEventBus mosipEventBus = retryStage.getEventBus(RetryStage.class);
		retryStage.consume(mosipEventBus, MessageBusAddress.RETRY_BUS);
		
	}
	@Override
	public MessageDTO process(MessageDTO dto) {
		
		RetryStage retryStage = new RetryStage();
		MosipEventBus mosipEventBus = retryStage.getEventBus(RetryStage.class);
		Integer retry=dto.getRetry()+1;
		dto.setRetry(retry);
		switch(dto.getAddress()) {
		
		case STRUCTURE_VALIDATION:
			
			retryStage.send(mosipEventBus, MessageBusAddress.STRUCTURE_BUS_IN, dto);
			
		case DEMOGRAPHIC_VALIDATION :
			retryStage.send(mosipEventBus, MessageBusAddress.DEMOGRAPHIC_BUS_IN, dto);
		
		case BIOMETRIC_VALIDATION:
			retryStage.send(mosipEventBus, MessageBusAddress.BIOMETRIC_BUS_IN, dto);
		
		default:
				break;
		}
		return dto;
	}

	

}
