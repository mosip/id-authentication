package io.mosip.authentication.core.function;

@FunctionalInterface
public interface SupplierWithThrowable<R, E extends Throwable> {

	R get() throws E;

}
