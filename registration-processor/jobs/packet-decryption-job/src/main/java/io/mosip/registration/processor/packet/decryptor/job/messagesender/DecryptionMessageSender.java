package io.mosip.registration.processor.packet.decryptor.job.messagesender;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;

@RefreshScope
@Service
public class DecryptionMessageSender extends MosipVerticleManager {

	private MosipEventBus mosipEventBus;

	@Value("${vertx.cluster.address}")
	private String clusterAddress;

	@Value("${vertx.localhost}")
	private String localhost;

	private void getEventBus() {
		if (this.mosipEventBus == null) {
			mosipEventBus = this.getEventBus(this.getClass(), clusterAddress, localhost);
		}
	}

	public void sendMessage(MessageDTO message) {
		getEventBus();
		this.send(this.mosipEventBus, MessageBusAddress.BATCH_BUS, message);
	}

	@Override
	public MessageDTO process(MessageDTO object) {

		return null;
	}
}
