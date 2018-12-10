package io.mosip.registration.processor.manual.adjudication.stage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;

/**
 * This class sends message to next stage after successful completion of manual verification
 * 
 * @author Pranav Kumar
 * @since 0.0.1
 *
 */
@Component
public class ManualVerificationStage extends MosipVerticleManager {

	/** The cluster address. */
	@Value("${registration.processor.vertx.cluster.address}")
	private String clusterAddress;

	/** The localhost. */
	@Value("${registration.processor.vertx.localhost}")
	private String localhost;

	private MosipEventBus mosipEventBus;

	public void sendMessage(MessageDTO messageDTO) {
		if (this.mosipEventBus == null) {
			this.mosipEventBus = this.getEventBus(this.getClass(), clusterAddress, localhost);
		}
		this.send(this.mosipEventBus, MessageBusAddress.MANUAL_VERIFICATION_BUS, messageDTO);
	}

	@Override
	public MessageDTO process(MessageDTO object) {
		return null;
	}

}
