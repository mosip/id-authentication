package io.mosip.registration.processor.core.spi.eventbus;

/**
 * @author Pranav Kumar
 * 
 * @author Mukul Puspam
 *
 * @param <T>
 *            The type specifying eventbus
 * @param <U>
 *            The type specifying address
 */
public interface EventBusManager<T, U> {

	/**
	 * @param instance
	 *            The class which needs to be setup
	 */
	public void setup(Class<?> instance);

	/**
	 * @param eventBus
	 *            The eventbus instance
	 * @param fromAddress
	 *            The address to consume an event from
	 * @param toAddress
	 *            The address to send an event to
	 */
	public void consumeAndSend(T eventBus, U fromAddress, U toAddress);

	/**
	 * @param object
	 *            The object for processing
	 * @return The object after processing
	 */
	public Object process(Object object);

}
