package io.mosip.registration.processor.packet.decryptor.job.messagesender;

import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;

public class DecryptionMessageSender extends MosipVerticleManager {
	
	public void sendMessage(MessageDTO message) {
		DecryptionMessageSender decryptionMessageSender = new DecryptionMessageSender();
		MosipEventBus mosipEventBus = decryptionMessageSender.getEventBus(DecryptionMessageSender.class);
		decryptionMessageSender.send(mosipEventBus, MessageBusAddress.STRUCTURE_BUS_IN, message);
	}

	@Override
	public Object process(Object object) {
		// TODO Auto-generated method stub
		return null;
	}

}
