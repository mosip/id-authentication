package io.mosip.kernel.core.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author M1037788
 *
 */
public final class ExceptionUtils {

	private ExceptionUtils() {

	}

	/**
	 * Returns an String object that can be used after building the exception stack
	 * trace.
	 * 
	 * @param message
	 *            the exception message
	 * @param cause
	 *            the cause
	 * @return the exception stack
	 */
	public static String buildMessage(String message, Throwable cause) {
		if (cause != null) {
			StringBuilder sb = new StringBuilder();
			if (message != null) {
				sb.append(message).append("; ");
			}
			sb.append("\n");
			sb.append("nested exception is ").append(cause);
			return sb.toString();
		} else {
			return message;
		}
	}

	/**
	 * This method returns the stack trace
	 * 
	 * @param throwable
	 *            the exception to be added to the list of exception
	 * @return the stack trace
	 */
	public static String getStackTrace(Throwable throwable) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		throwable.printStackTrace(pw);
		return sw.getBuffer().toString();
	}

}
