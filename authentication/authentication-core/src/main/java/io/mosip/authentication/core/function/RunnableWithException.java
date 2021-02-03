package io.mosip.authentication.core.function;

@FunctionalInterface
public interface RunnableWithException<E extends Exception> {

	void run() throws E;

}
