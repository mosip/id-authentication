package io.mosip.registration.processor.core.spi.eventbus;

public interface EventBusManager<T, U>{

	public T getEventBus(Class<?> instance);
	
	public void consumeAndSend(T eventBus, U fromAddress, U toAddress);
	
	public Object process(Object object);
	
}
