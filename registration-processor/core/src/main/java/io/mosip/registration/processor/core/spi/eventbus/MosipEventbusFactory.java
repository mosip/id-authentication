package io.mosip.registration.processor.core.spi.eventbus;

/**
 * Declares the Eventbus.
 * 
 * @param <T>
 *            The type of underlying Eventbus
 * 
 * @author Pranav Kumar
 * 
 * @since 0.0.1
 */
public interface MosipEventbusFactory<T> {

	public T getEventbus();

}
