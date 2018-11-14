package io.mosip.kernel.jsonvalidator.constant;

/**
 * Enum containing custom error codes and the respective messages.
 * 
 * @author Swati Raj
 * @since 1.0.0
 *
 */
public enum JsonValidatorErrorConstant {

	FILE_IO_EXCEPTION("KER-JVL-001", "IO interruption while reading the schema file with the name provided."),
	JSON_VALIDATION_PROCESSING_EXCEPTION("KER-JVL-002", "JSON Valdation Processing Interrupted"),
	UNIDENTIFIED_JSON_EXCEPTION("KER-JVL-003", "JSON object does not match with the Schema Definition"),
	NULL_JSON_NODE_EXCEPTION("KER-JVL-004", "Null input json String"),
	JSON_IO_EXCEPTION("KER-JVL-005", "Invalid input JSON String"),
	HTTP_REQUEST_EXCEPTION("KER-JVL-006", "Unable get to JSON schema with given name from config server"),
	CONFIG_SERVER_CONNECTION_EXCEPTION("KER-JVL-007", "Unable to connect to Configuration Server"),
	JSON_SCHEMA_IO_EXCEPTION("KER-JVL-008", "Invalid JSON Schema"),
	NULL_JSON_SCHEMA_EXCEPTION("KER-JVL-009", "Null input json Schema");

	public final String errorCode;

	public final String message;

	JsonValidatorErrorConstant(final String errorCode, final String message) {
		this.errorCode = errorCode;
		this.message = message;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getMessage() {
		return message;
	}



}
