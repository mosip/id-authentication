package io.mosip.registration.processor.core.spi.queue;

public interface MosipQueue<T,U, V, Q> extends MosipQueueFactory<Q> {
	
	public T getConnection(U username, U password, U url);
	
	public Boolean send(V message, U address);
	
	public V consume(U address);

}
