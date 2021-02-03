package io.mosip.authentication.core.function;

@FunctionalInterface
public interface FunctionWithException<R, T, E extends Exception> {

	R apply(T t) throws E;

}
