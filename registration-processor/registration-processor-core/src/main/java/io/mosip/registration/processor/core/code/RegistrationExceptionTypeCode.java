package io.mosip.registration.processor.core.code;

/**
 * The Enum RegistrationExceptionTypeCode.
 */
public enum RegistrationExceptionTypeCode {

	/** The table not accessible exception. */
	TABLE_NOT_ACCESSIBLE_EXCEPTION,

	/** The internal server error. */
	INTERNAL_SERVER_ERROR,

	/** The packet not found exception. */
	PACKET_NOT_FOUND_EXCEPTION,

	/** The packet uploaded to landing zone. */
	PACKET_UPLOADED_TO_LANDING_ZONE,

	/** The unexcepted error. */
	UNEXCEPTED_ERROR,

	/** The fsadapter exception. */
	FSADAPTER_EXCEPTION,

	JSCH_CONNECTION,

	SFTP_OPERATION_EXCEPTION,

	/** The unsupported encoding exception. */
	UNSUPPORTED_ENCODING_EXCEPTION,

	/** The parse exception. */
	PARSE_EXCEPTION,

	/** The data access exception. */
	DATA_ACCESS_EXCEPTION,

	/** The data access layer exception. */
	DATA_ACCESS_LAYER_EXCEPTION,

	/** The duplicate upload request exception. */
	DUPLICATE_UPLOAD_REQUEST_EXCEPTION,

	/** The exception. */
	EXCEPTION,

	/** The interrupted exception. */
	INTERRUPTED_EXCEPTION,

	/** The execution exception. */
	EXECUTION_EXCEPTION,

	/** The unknown host exception. */
	UNKNOWN_HOST_EXCEPTION,

	/** The jms exception. */
	JMS_EXCEPTION,

	/** The invalid key spec exception. */
	INVALID_KEY_SPEC_EXCEPTION,

	/** The illegal argument exception. */
	ILLEGAL_ARGUMENT_EXCEPTION,

	/** The invalid id exception. */
	INVALID_ID_EXCEPTION,

	/** The instantiation exception. */
	INSTANTIATION_EXCEPTION,

	/** The illegal access exception. */
	ILLEGAL_ACCESS_EXCEPTION,

	/** The no such field exception. */
	NO_SUCH_FIELD_EXCEPTION,

	/** The security exception. */
	SECURITY_EXCEPTION,

	/** The template not found exception. */
	TEMPLATE_NOT_FOUND_EXCEPTION,

	/** The template processing failure exception. */
	TEMPLATE_PROCESSING_FAILURE_EXCEPTION,

	/** The template resource not found exception. */
	TEMPLATE_RESOURCE_NOT_FOUND_EXCEPTION,

	/** The template parsing exception. */
	TEMPLATE_PARSING_EXCEPTION,

	/** The template method invocation exception. */
	TEMPLATE_METHOD_INVOCATION_EXCEPTION,

	/** The apis resource access exception. */
	APIS_RESOURCE_ACCESS_EXCEPTION,

	/** The json parse exception. */
	JSON_PARSE_EXCEPTION,

	/** The json mapping exception. */
	JSON_MAPPING_EXCEPTION,

	/** The json processing exception. */
	JSON_PROCESSING_EXCEPTION,

	/** The json io exception. */
	JSON_IO_EXCEPTION,

	/** The json schema io exception. */
	JSON_SCHEMA_IO_EXCEPTION,

	/** The file not found exception. */
	FILE_NOT_FOUND_EXCEPTION,

	/** The file not found in destination exception. */
	FILE_NOT_FOUND_IN_DESTINATION_EXCEPTION,

	/** The no such algorithm exception. */
	NO_SUCH_ALGORITHM_EXCEPTION,

	/** The run time exception. */
	RUN_TIME_EXCEPTION,

	/** The mosip invalid data exception. */
	MOSIP_INVALID_DATA_EXCEPTION,

	/** The mosip invalid keyexception. */
	MOSIP_INVALID_KEY_EXCEPTION,

	/** The class not found exception. */
	CLASS_NOT_FOUND_EXCEPTION,

	/** The reg status validation exception. */
	REG_STATUS_VALIDATION_EXCEPTION,

	/** The packet decryption failure exception. */
	PACKET_DECRYPTION_FAILURE_EXCEPTION,

	/** The configuration not found exception. */
	CONFIGURATION_NOT_FOUND_EXCEPTION,

	/** The identity not found exception. */
	IDENTITY_NOT_FOUND_EXCEPTION,

	/** The queue connection not found. */
	QUEUE_CONNECTION_NOT_FOUND,

	/** The connection unavailable exception. */
	CONNECTION_UNAVAILABLE_EXCEPTION,

	/** The virus scan failed exception. */
	VIRUS_SCAN_FAILED_EXCEPTION,

	/** The virus scanner service failed. */
	VIRUS_SCANNER_SERVICE_FAILED,

	/** The ioexception. */
	IOEXCEPTION,

	/** The file io exception. */
	FILE_IO_EXCEPTION,

	/** The packet osi validation failed. */
	PACKET_OSI_VALIDATION_FAILED,

	/** The packet structural validation failed. */
	PACKET_STRUCTURAL_VALIDATION_FAILED,

	/** The packet uploader failed. */
	PACKET_UPLOADER_FAILED,

	/** The packet uin generation failed. */
	PACKET_UIN_GENERATION_FAILED,

	/** The invocation target exception. */
	INVOCATION_TARGET_EXCEPTION,

	/** The introspection exception. */
	INTROSPECTION_EXCEPTION,

	/** The base unchecked exception. */
	BASE_UNCHECKED_EXCEPTION,

	/** The external integration failed. */
	EXTERNAL_INTEGRATION_FAILED,
	
	/** The registration processor checked exception. */
	REGISTRATIONPROCESSORCHECKEDEXCEPTION,

	ADULT_CBEFF_NOT_PRESENT_EXCEPTION,
	
	DEMO_DEDUPE_ABIS_RESPONSE_ERROR,

}
