package io.mosip.registration.processor.packet.storage.exception;

public enum RPR_PLATFORM_ERROR_MESSAGES {
	
	TABLE_NOT_ACCESSIBLE("File not found in DFS Location"),
	IDENTITY_NOT_FOUND("identity field not found in DemographicInfo Json"),
	UNABLE_TO_INSERT_DATA("Unable to insert data in db for registration  :"),
	FILE_NOT_FOUND_IN_DFS("File not found in DFS or file is null "),
	IDENTITY_JSON_MAPPING_EXCEPTION("Error while mapping Identity Json"),
	JSON_PARSING_EXCEPTION("Error while parsing Json"),
	INSTANTIATION_EXCEPTION("Error while creating object of JsonValue class"),
	PARSING_DATE_EXCEPTION("Error while parsing date "),
	UNABLE_TO_CONVERT_STREAM_TO_BYTES("Error while converting inputstream to bytes"),
	NO_SUCH_FIELD_EXCEPTION(" Could not find the field ");
	
	private final String errorMessage;

	private RPR_PLATFORM_ERROR_MESSAGES(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getValue() {
		return errorMessage;
	}

}
