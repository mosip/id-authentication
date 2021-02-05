package io.mosip.authentication.core.retry;

import java.util.function.Supplier;

public class FailingMockOperationWithRetry<T extends Exception> extends FailingMockOperation<T> {

	public FailingMockOperationWithRetry(int failTimes, Supplier<T> exceptionSupplier) {
		super(failTimes, exceptionSupplier);
	}

	public FailingMockOperationWithRetry(Supplier<T> exceptionSupplier) {
		super(exceptionSupplier);
	}
	
	@WithRetry
	@Override
	public Object get() throws T {
		return super.get();
	}
	
	@WithRetry
	@Override
	public void run() throws T {
		super.run();
	}
	
	@WithRetry
	@Override
	public void accept(Object obj) throws T {
		super.accept(obj);
	}
	
	@WithRetry
	@Override
	public Object apply(Object obj) throws T {
		return super.apply(obj);
	}

}