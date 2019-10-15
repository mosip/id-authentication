/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.document.errorcodes;

/**
 * 
 * This Enum provides the constant variables to define Error Messages.
 * 
 * @author Ravi C Balaji
 * @since 1.0.0
 *
 */
public enum ErrorMessages {

	/**
	 * ErrorMessage for PRG_PAM_DOC_001
	 */
	DOCUMENT_FAILED_IN_QUALITY_CHECK("Document failde in quality check"),
	/**
	 * ErrorMessage for PRG_PAM_DOC_002
	 */
	DOCUMENT_FAILED_IN_ENCRYPTION("Document failed to encrypt"),
	/**
	 * ErrorMessage for PRG_PAM_DOC_003
	 */
	DOCUMENT_FAILED_IN_DECRYPTION("Document failed to decrypt"),
	/**
	 * ErrorMessage for PRG_PAM_DOC_004
	 */
	DOCUMENT_INVALID_FORMAT("Invalid document format supported"),
	/**
	 * ErrorMessage for PRG_PAM_DOC_005
	 */
	DOCUMENT_NOT_PRESENT("Document not found for the source pre-registration Id"),
	/**
	 * ErrorMessage for PRG_PAM_DOC_005
	 */
	DOCUMENT_FAILED_TO_FETCH("Failed to fetch from File System server"),
	/**
	 * ErrorMessage for PRG_PAM_DOC_006
	 */
	DOCUMENT_FAILED_TO_DELETE("Documents failed to delete"),
	/**
	 * ErrorMessage for PRG_PAM_DOC_007
	 */
	DOCUMENT_EXCEEDING_PREMITTED_SIZE("Document exceeding permitted size"),
	/**
	 * ErrorMessage for PRG_PAM_DOC_008
	 */
	DOCUMENT_TYPE_NOT_SUPPORTED("Document type not supported"),
	/**
	 * ErrorMessage for PRG_PAM_DOC_009
	 */
	DOCUMENT_FAILED_TO_UPLOAD("Document upload failed"),
	/**
	 * ErrorMessage for PRG_PAM_DOC_010
	 */
	DOCUMENT_FAILED_IN_VIRUS_SCAN("Document virus scan failed"),
	/**
	 * ErrorMessage for PRG_PAM_DOC_011
	 */
	DOCUMENT_FAILED_TO_COPY("Document copy failed from source to destination"),
	/**
	 * ErrorMessage for PRG_PAM_DOC_012
	 */
	DOCUMENT_TABLE_NOTACCESSIBLE("Document table not accessible"),
	/**
	 * ErrorMessage for PRG_PAM_DOC_013
	 */
	DOCUMENT_IO_EXCEPTION("Document I/O exception"),
	/**
	 * ErrorMessage for PRG_PAM_DOC_014
	 */
	MANDATORY_FIELD_NOT_FOUND("Mandatory field not found"),
	/**
	 * ErrorMessage for PRG_PAM_DOC_015
	 */
	JSON_EXCEPTION("Json exception"),
	/**
	 * ErrorMessage for PRG_PAM_DOC_016
	 */
	INVALID_CEPH_CONNECTION("CEPH connection is invalid"),
	/**
	 * ErrorMessage for PRG_PAM_DOC_017
	 */
	CONNECTION_UNAVAILABLE("Connection is not available"),
	/**
	 * ErrorMessage for PRG_PAM_DOC_018
	 */
	INVALID_REQUEST_PARAMETER("Invalid request parameter"),

	/**
	 * ErrorMessage for PRG_PAM_DOC_019
	 */
	INVALID_DOCUMENT_ID("DocumentId is not belongs to the pre-registration Id"),
	/**
	 * ErrorMessage for PRG_PAM_DOC_019
	 */
	DEMOGRAPHIC_DATA_NOT_FOUND("Demographic data not found for the preRegistrationId"),
	/**
	 * ErrorMessage for PRG_PAM_DOC_019
	 */
	DEMOGRAPHIC_GET_RECORD_FAILED("Demographic record failed to fetch"),

	/**
	 * ErrorMessage for PRG_PAM_DOC_018
	 */
	INVALID_PRE_ID("PreRegistrationId is invalid"),

	/**
	 * ErrorMessage for PRG_PAM_DOC_018
	 */
	INVALID_STATUS_CODE("Status code is invalid"),

	/**
	 * ErrorMessage for PRG_PAM_DOC_018
	 */
	INVALID_UPLOAD_BY("Uploaded by is invalid"),

	/**
	 * ErrorMessage for PRG_PAM_DOC_018
	 */
	INVALID_UPLOAD_DATE_TIME("Updated date and time is invalid"),

	/**
	 * ErrorMessage for PRG_PAM_DOC_021
	 */
	DOCUMENT_ALREADY_PRESENT("Document is alredy present");

	private ErrorMessages(String message) {
		this.message = message;
	}

	private final String message;

	/**
	 * @return message
	 */
	public String getMessage() {
		return message;
	}
}
