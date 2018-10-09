package io.mosip.registration.processor.packet.decryptor.job.messagesender;

import org.springframework.stereotype.Service;

import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;

@Service
public class DecryptionMessageSender extends MosipVerticleManager {
	
	private MosipEventBus mosipEventBus;
	
	private void getEventBus() {
		if(this.mosipEventBus == null) {
			mosipEventBus = this.getEventBus(this.getClass());
		}
	}
	
	public void sendMessage(MessageDTO message) {
		getEventBus();
		this.send(this.mosipEventBus, MessageBusAddress.STRUCTURE_BUS_IN, message);
	}

	@Override
	public MessageDTO process(MessageDTO object) {
		// TODO Auto-generated method stub
		return null;
	}
}
