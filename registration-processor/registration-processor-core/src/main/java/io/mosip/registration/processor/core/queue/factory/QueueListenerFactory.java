package io.mosip.registration.processor.core.queue.factory;

import javax.jms.Message;
import javax.jms.MessageListener;

public class QueueListenerFactory {


	private QueueListenerFactory() {
	}

	public static MessageListener getListener(String queueName, QueueListener object) {
		if(queueName.equals("ACTIVEMQ")){
			return new ActiveMQMessageListener() {				
				@Override
				public void onMessage(Message message) {
					object.setListener(message);
				}
			};			
		}
		return null;
	}

}
