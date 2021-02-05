package io.mosip.authentication.core.retry;

import java.util.function.Supplier;

/**
 * The Class FailingMockOperation used in tests.
 *
 * @param <T> the generic type
 * 
 * @author Loganathan Sekar
 * 
 */
public class FailingMockOperation<T extends Exception> {
		
		/** The fail times. */
		private final int failTimes;
		
		/** The executed times. */
		private int executedTimes = 0;
		
		/** The exception supplier. */
		private Supplier<T> exceptionSupplier;
		
		/**
		 * Instantiates a new failing mock operation.
		 *
		 * @param exceptionSupplier the exception supplier
		 */
		public FailingMockOperation(Supplier<T> exceptionSupplier) {
			this(0,exceptionSupplier);
		}

		/**
		 * Instantiates a new failing mock operation.
		 *
		 * @param failTimes the fail times
		 * @param exceptionSupplier the exception supplier
		 */
		public FailingMockOperation(int failTimes, Supplier<T> exceptionSupplier) {
			this.failTimes = failTimes;
			this.exceptionSupplier = exceptionSupplier;
		}
		
		/**
		 * Gets the.
		 *
		 * @return the object
		 * @throws T the t
		 */
		public Object get() throws T {
			if(executedTimes == failTimes) {
				executedTimes+=1;
				return new Object();
			} else {
				executedTimes+=1;
				throw exceptionSupplier.get();
			}
		}
		
		/**
		 * Run.
		 *
		 * @throws T the t
		 */
		public void run() throws T {
			if(executedTimes == failTimes) {
				executedTimes+=1;
				return;
			} else {
				executedTimes+=1;
				throw exceptionSupplier.get();
			}
		}
		
		/**
		 * Accept.
		 *
		 * @param obj the obj
		 * @throws T the t
		 */
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
		
		/**
		 * Apply.
		 *
		 * @param obj the obj
		 * @return the object
		 * @throws T the t
		 */
		public Object apply(Object obj) throws T {
			if(executedTimes == failTimes) {
				executedTimes+=1;
				return obj;
			} else {
				executedTimes+=1;
				throw exceptionSupplier.get();
			}
		}
		
		/**
		 * Gets the executed times.
		 *
		 * @return the executed times
		 */
		public int getExecutedTimes() {
			return executedTimes;
		}
	}