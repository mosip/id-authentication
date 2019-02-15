package io.mosip.registration.processor.core.spi.queue;

public interface MosipQueueConnectionFactory<Q> {
	
	public Q createConnection(String typeOfQueue, String username, String password, String Url);

}
