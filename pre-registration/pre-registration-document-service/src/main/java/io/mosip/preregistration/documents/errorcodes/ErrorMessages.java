/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.documents.errorcodes;

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
	DOCUMENT_FAILED_IN_QUALITY_CHECK,
	/**
	 * ErrorMessage for PRG_PAM_DOC_002
	 */
	DOCUMENT_FAILED_IN_ENCRYPTION,
	/**
	 * ErrorMessage for PRG_PAM_DOC_003
	 */
	DOCUMENT_FAILED_IN_DECRYPTION,
	/**
	 * ErrorMessage for PRG_PAM_DOC_004
	 */
	DOCUMENT_INVALID_FORMAT,
	/**
	 * ErrorMessage for PRG_PAM_DOC_005
	 */
	DOCUMENT_NOT_PRESENT,
	/**
	 * ErrorMessage for PRG_PAM_DOC_005
	 */
	DOCUMENT_FAILED_TO_FETCH,
	/**
	 * ErrorMessage for PRG_PAM_DOC_006
	 */
	DOCUMENT_FAILED_TO_DELETE,
	/**
	 * ErrorMessage for PRG_PAM_DOC_007
	 */
	DOCUMENT_EXCEEDING_PREMITTED_SIZE,
	/**
	 * ErrorMessage for PRG_PAM_DOC_008
	 */
	DOCUMENT_TYPE_NOT_SUPPORTED,
	/**
	 * ErrorMessage for PRG_PAM_DOC_009
	 */
	DOCUMENT_FAILED_TO_UPLOAD,
	/**
	 * ErrorMessage for PRG_PAM_DOC_010
	 */
	DOCUMENT_FAILED_IN_VIRUS_SCAN,
	/**
	 * ErrorMessage for PRG_PAM_DOC_011
	 */
	DOCUMENT_FAILED_TO_COPY,
	/**
	 * ErrorMessage for PRG_PAM_DOC_012
	 */
	DOCUMENT_TABLE_NOTACCESSIBLE,
	/**
	 * ErrorMessage for PRG_PAM_DOC_013
	 */
	DOCUMENT_IO_EXCEPTION,
	/**
	 * ErrorMessage for PRG_PAM_DOC_014
	 */
	MANDATORY_FIELD_NOT_FOUND,
	/**
	 * ErrorMessage for PRG_PAM_DOC_015
	 */
	JSON_EXCEPTION,
	/**
	 * ErrorMessage for PRG_PAM_DOC_016
	 */
	INVALID_CEPH_CONNECTION,
	/**
	 * ErrorMessage for PRG_PAM_DOC_017
	 */
	CONNECTION_UNAVAILABLE,
	/**
	 * ErrorMessage for PRG_PAM_DOC_018
	 */
	INVALID_REQUEST_PARAMETER,

	/**
	 * ErrorMessage for PRG_PAM_DOC_019
	 */
	INVALID_DOCUMENT_ID,
	/**
	 * ErrorMessage for PRG_PAM_DOC_019
	 */
	INVALID_DOCUMENT_CATEGORY_CODE,
	
	/**
	 * ErrorMessage for PRG_PAM_DOC_019
	 */
	DEMOGRAPHIC_DATA_NOT_FOUND,
	/**
	 * ErrorMessage for PRG_PAM_DOC_019
	 */
	DEMOGRAPHIC_GET_RECORD_FAILED;
}
