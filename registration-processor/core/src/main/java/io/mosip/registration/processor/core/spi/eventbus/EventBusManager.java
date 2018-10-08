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

	public T getEventBus(Class<?> instance);

	public void consumeAndSend(T eventBus, U fromAddress, U toAddress);

	public V process(V object);

}
