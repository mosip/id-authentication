package io.mosip.registration.processor.core.spi.eventbus;

public interface MosipEventbusFactory<T> {

	public T getEventbus();

}
