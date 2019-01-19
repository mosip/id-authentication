package io.mosip.registration.processor.failoverstage;

import org.springframework.stereotype.Service;

import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;


/**
 * The Class FailoverStage.
 */
@Service
public class FailoverStage extends MosipVerticleManager {
    /**
     * Deploy failover stage.
     */	
	public void deployFailoverStage() {
		// this.getEventBus(this.getClass());
	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.processor.core.spi.eventbus.EventBusManager#process(java.lang.Object)
	 */
	@Override
	public MessageDTO process(MessageDTO object) {
		return null;
	}
}
