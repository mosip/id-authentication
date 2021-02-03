package io.mosip.authentication.core.function;

@FunctionalInterface
public interface SupplierWithException<R, E extends Exception> {

	R get() throws E;

}
