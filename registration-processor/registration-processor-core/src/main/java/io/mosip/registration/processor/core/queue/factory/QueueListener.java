package io.mosip.registration.processor.core.queue.factory;

import javax.jms.Message;

public abstract class QueueListener {
	
	public abstract void setListener(Message message);
}