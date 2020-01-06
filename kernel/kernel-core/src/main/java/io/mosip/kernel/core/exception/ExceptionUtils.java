package io.mosip.kernel.core.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This utils contains exception utilities.
 * 
 * @author Shashank Agrawal
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
public final class ExceptionUtils {

	private static final Logger logger = LoggerFactory.getLogger(ExceptionUtils.class);

	private ExceptionUtils() {

	}

	/**
	 * Returns an String object that can be used after building the exception stack
	 * trace.
	 * 
	 * @param message the exception message
	 * @param cause   the cause
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
	 * @param throwable the exception to be added to the list of exception
	 * @return the stack trace
	 */
	public static String getStackTrace(Throwable throwable) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		throwable.printStackTrace(pw);
		return sw.getBuffer().toString();
	}

	/**
	 * This method gives service error list for response receive from service.
	 * 
	 * @param responseBody the service response body.
	 * @return the list of {@link ServiceError}
	 */
	public static List<ServiceError> getServiceErrorList(String responseBody) {
		ObjectMapper mapper = new ObjectMapper();

		List<ServiceError> validationErrorsList = new ArrayList<>();

		try {
			JsonNode errorResponse = mapper.readTree(responseBody);

			if (errorResponse.has("errors")) {

				JsonNode errors = errorResponse.get("errors");

				Iterator<JsonNode> iter = errors.iterator();

				while (iter.hasNext()) {
					JsonNode parameterNode = iter.next();
					ServiceError serviceError = new ServiceError(getJsonValue(parameterNode, "errorCode"),
							getJsonValue(parameterNode, "message"));
					validationErrorsList.add(serviceError);
				}
			}
		} catch (Exception e) {
			// There is no Service error
		}

		return validationErrorsList;

	}

	/**
	 * This method provide jsonvalue based on propname mention.
	 * 
	 * @param node     the jsonnode.
	 * @param propName the property name.
	 * @return the property value.
	 */
	private static String getJsonValue(JsonNode node, String propName) {
		if (node.get(propName) != null) {
			return node.get(propName).asText();
		}
		return null;
	}

	public static void logRootCause(Throwable exception) {
		System.out.println("\n\n");
//		Optional<Throwable> rootCause = Stream.iterate(exception, Throwable::getCause)
//				.filter(element -> element.getCause() == null).findFirst();
//		if (rootCause.isPresent()) {
//			logger.error("Error : " + exception.getMessage());
//			logger.error("RootCause Error : ", rootCause.get());
//		} else {
//			logger.error("Error : ", exception);
//		}

		// System.out.println("Error : " + exception.getMessage());
		exception.printStackTrace();

		System.out.println("\n");
	}
}
