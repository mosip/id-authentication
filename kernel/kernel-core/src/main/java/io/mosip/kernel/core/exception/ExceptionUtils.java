package io.mosip.kernel.core.exception;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

	/**
	 * This method gives service error list for response receive from service.
	 * 
	 * @param responseBody
	 *            the service response body.
	 * @return the list of {@link ServiceError}
	 * @throws IOException
	 */
	public static List<ServiceError> getServiceErrorList(String responseBody) throws IOException {
		ObjectMapper mapper = new ObjectMapper();

		List<ServiceError> validationErrorsList = new ArrayList<>();

		JsonNode errorResponse = mapper.readTree(responseBody);

		if (errorResponse.has("errors")) {

			JsonNode errors = errorResponse.get("errors");

			Iterator<JsonNode> iter = errors.iterator();

			while (iter.hasNext()) {
				JsonNode parameterNode = iter.next();
				ServiceError serviceError = new ServiceError(getJsonValue(parameterNode, "errorCode"),
						getJsonValue(parameterNode, "errorMessage"));
				validationErrorsList.add(serviceError);
			}
		}

		return validationErrorsList;

	}

	/**
	 * This method provide jsonvalue based on propname mention.
	 * 
	 * @param node
	 *            the jsonnode.
	 * @param propName
	 *            the property name.
	 * @return the property value.
	 */
	private static String getJsonValue(JsonNode node, String propName) {
		if (node.get(propName) != null) {
			return node.get(propName).asText();
		}
		return null;
	}
}
