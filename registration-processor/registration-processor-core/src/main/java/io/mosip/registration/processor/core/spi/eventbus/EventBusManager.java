package io.mosip.registration.processor.core.spi.eventbus;

/**
 * Declares all the methods to be used by Processor stages
 *
 * @param <T>
 *            The type of underlying Eventbus
 * @param <U>
 *            The type of address for communication between stages
 * @param <V>
 *            The type of Message for communication between stages
 * 
 * @author Pranav Kumar
 * @since 0.0.1
 */
public interface EventBusManager<T, U, V> {

	/**
	 * This method returns the EventBus instance for the provided class
	 * 
	 * @param instance
	 *            The class for which eventBus is needed
	 * @return The EventBus instance
	 * @throws InterruptedException
	 */
	public T getEventBus(Class<?> clazz, String clusterManagerUrl);

	/**
	 * This method consumes a message from an address, processes it and forwards the
	 * message to next given address
	 * 
	 * @param eventBus
	 *            The Eventbus instance for communication
	 * @param fromAddress
	 *            The address from which message is to be consumed
	 * @param toAddress
	 *            The address to which message needs to be sent
	 */
	public void consumeAndSend(T eventBus, U fromAddress, U toAddress);

	/**
	 * This method processes on the supplied object and returns the modified object
	 * 
	 * @param object
	 *            The object for processing
	 * @return The modified object after processing
	 */
	public V process(V object);

}
