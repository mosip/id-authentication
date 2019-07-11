package io.mosip.registration.processor.core.queue.factory;

import javax.jms.Message;
import javax.jms.MessageListener;

public abstract class ActiveMQMessageListener implements MessageListener {

	@Override
	public abstract void onMessage(Message message);

}
