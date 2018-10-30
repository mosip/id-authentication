package io.mosip.registration.processor.failoverstage;

import org.springframework.stereotype.Service;

import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;

@Service
public class FailoverStage extends MosipVerticleManager {

	public void deployFailoverStage() {
		// this.getEventBus(this.getClass());
	}

	@Override
	public MessageDTO process(MessageDTO object) {
		return null;
	}
}
