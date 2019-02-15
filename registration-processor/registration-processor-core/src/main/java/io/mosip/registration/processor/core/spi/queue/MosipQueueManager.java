package io.mosip.registration.processor.core.spi.queue;

/**
 * @author Pranav Kumar
 * 
 * @param <V> Type of Message
 * @param <Q> Type Of Queue
 */
public interface MosipQueueManager<T, V, Q> extends MosipQueueConnectionFactory<Q> {

	/**
	 * This method sends a message on a given Address
	 * 
	 * @param mosipQueue The mosipQueue instance
	 * @param message    The message
	 * @param address    The address
	 * @return True if message is sent, false otherwise
	 */
	public Boolean send(T mosipQueue, V message, String address);

	/**
	 * This method consumes a message from a given address
	 * 
	 * @param mosipQueue The mosipQueue instance
	 * @param address    The address
	 * @return the original message
	 */
	public V consume(T mosipQueue, String address);

}
