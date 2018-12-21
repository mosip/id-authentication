package io.mosip.registration.processor.packet.receiver.stage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;

@Service
public class PacketReceiverStage extends MosipVerticleManager {

	/** The cluster address. */
	@Value("${registration.processor.vertx.cluster.address}")
	private String clusterAddress;

	/** The localhost. */
	@Value("${registration.processor.vertx.localhost}")
	private String localhost;

	private MosipEventBus mosipEventBus;

	public void deployStage() {
		if (this.mosipEventBus == null) {
			this.mosipEventBus = this.getEventBus(this.getClass(), clusterAddress, localhost);
		}
	}

	public void sendMessage(MessageDTO messageDTO) {
		this.send(this.mosipEventBus, MessageBusAddress.VIRUS_SCAN_BUS_IN, messageDTO);
	}

	@Override
	public MessageDTO process(MessageDTO object) {
		return null;
	}

}
