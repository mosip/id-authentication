package io.mosip.pregistration.datasync.code;

/**
 * 
 * Various Status codes for Data Sync
 * 
 * @author M1046129 - Jagadishwari
 *
 */
public enum StatusCodes {

	DOCUMENT_NOT_PRESENT_REQUEST, 
	DOCUMENT_IS_MISSING, 
	DOCUMENT_TABLE_NOTACCESSIBLE, 
	FAILED_TO_CREATE_A_ZIP_FILE, 
	RECORDS_NOT_FOUND_FOR_REQUESTED_PREREGID, 
	PRE_REGISTRATION_IDS_STORED_SUCESSFULLY, 
	FAILED_TO_STORE_PRE_REGISTRATION_IDS;
}
