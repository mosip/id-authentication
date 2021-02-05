package io.mosip.authentication.core.function;

@FunctionalInterface
public interface RunnableWithThrowable<E extends Throwable> {

	void run() throws E;

}
