package io.mosip.registration.processor.core.queue.factory;

public abstract class MosipQueue{
	
	public abstract void createConnection(String username, String password, String brokerUrl);
	
	public abstract String getQueueName();

}
