package io.mosip.kernel.core.idobjectvalidator.constant;

/**
 * Enum containing custom error codes and the respective messages.
 * 
 * @author Swati Raj
 * @since 1.0.0
 *
 */
public enum IdObjectValidatorErrorConstant {

	FILE_IO_EXCEPTION("KER-JVL-001", "IO interruption while reading the schema file with the name provided."),
	
	ID_OBJECT_VALIDATION_FAILED("KER-JVL-002", "Id Object validation failed"),
	
	UNIDENTIFIED_JSON_EXCEPTION("KER-JVL-003", "JSON object does not match with the Schema Definition"),
	
	NULL_JSON_NODE_EXCEPTION("KER-JVL-004", "Null input json String"),
	
	ID_OBJECT_IO_EXCEPTION("KER-JVL-005", "Invalid input Identity object"),
	
	HTTP_REQUEST_EXCEPTION("KER-JVL-006", "Unable get to JSON schema with given name from config server"),
	
	CONFIG_SERVER_CONNECTION_EXCEPTION("KER-JVL-007", "Unable to connect to Configuration Server"),
	
	JSON_SCHEMA_IO_EXCEPTION("KER-JVL-008", "Invalid JSON Schema"),
	
	NULL_JSON_SCHEMA_EXCEPTION("KER-JVL-009", "Null input json Schema"),
	
	INVALID_INPUT_PARAMETER("KER-JVL-010", "Invalid input parameter - %s");

	public final String errorCode;

	public final String message;

	/**
	 * Instantiates a new json validator error constant.
	 *
	 * @param errorCode the error code
	 * @param message the message
	 */
	IdObjectValidatorErrorConstant(final String errorCode, final String message) {
		this.errorCode = errorCode;
		this.message = message;
	}

	/**
	 * Gets the error code.
	 *
	 * @return the error code
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

}
