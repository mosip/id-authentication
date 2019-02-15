package io.mosip.registration.processor.core.spi.queue;

public interface MosipQueueFactory<Q> {
	
	public Q getQueue(String typeOfQueue);

}
