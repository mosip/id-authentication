/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.document.code;

/**
 * 
 * This Enum provides constants to define Status codes for Document service.
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */
public enum DocumentStatusMessages {

	DOCUMENT_FOR_VIRUS_SCAN("Document virus scan failed"),
	DOCUMENT_UPLOAD_SUCCESSFUL("Document is successfully uploaded"),
	DOCUMENT_EXCEEDING_PERMITTED_SIZE("Document exceeding permitted size"),
	DOCUMENT_NOT_PRESENT_REQUEST("Documents is not found for the requested pre-registration id"),
	FILE_TYPE_NOT_SUPPORTED("File Type is not supported"),
	DOCUMENT_INVALID_FORMAT("Document format is invalid"),
	DOCUMENT_IS_MISSING("Documents is not found for the requested pre-registration id"),
	ALL_DOCUMENT_DELETE_SUCCESSFUL("All documents assosiated with requested pre-registration id deleted sucessfully"),
	DOCUMENT_DELETE_SUCCESSFUL("Document successfully deleted"),
	DOCUMENT_TABLE_NOTACCESSIBLE("Document table not accessible"),
	DOCUMENT_NOT_AVAILABLE("Documents is not found for the requested pre-registration id"),
	DOCUMENT_COPY_SUCCESSFUL("Document copied successfully");
	
	private DocumentStatusMessages(String message) {
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
