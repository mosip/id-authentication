package io.mosip.authentication.core.retry;

import java.util.function.Supplier;

public class FailingMockOperation<T extends Exception> {
		private final int failTimes;
		private int executedTimes = 0;
		private Supplier<T> exceptionSupplier;
		
		public FailingMockOperation(Supplier<T> exceptionSupplier) {
			this(0,exceptionSupplier);
		}

		public FailingMockOperation(int failTimes, Supplier<T> exceptionSupplier) {
			this.failTimes = failTimes;
			this.exceptionSupplier = exceptionSupplier;
		}
		
		public Object get() throws T {
			if(executedTimes == failTimes) {
				executedTimes+=1;
				return new Object();
			} else {
				executedTimes+=1;
				throw exceptionSupplier.get();
			}
		}
		
		public void run() throws T {
			if(executedTimes == failTimes) {
				executedTimes+=1;
				return;
			} else {
				executedTimes+=1;
				throw exceptionSupplier.get();
			}
		}
		
		public void accept(Object obj) throws T {
			if(executedTimes == failTimes) {
				executedTimes+=1;
				System.out.println(obj);
				return;
			} else {
				executedTimes+=1;
				throw exceptionSupplier.get();
			}
		}
		
		public Object apply(Object obj) throws T {
			if(executedTimes == failTimes) {
				executedTimes+=1;
				return obj;
			} else {
				executedTimes+=1;
				throw exceptionSupplier.get();
			}
		}
		
		public int getExecutedTimes() {
			return executedTimes;
		}
	}