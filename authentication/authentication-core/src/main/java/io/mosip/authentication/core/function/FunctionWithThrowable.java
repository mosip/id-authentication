package io.mosip.authentication.core.function;

@FunctionalInterface
public interface FunctionWithThrowable<R, T, E extends Throwable> {

	R apply(T t) throws E;

}
