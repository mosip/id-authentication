package io.mosip.pregistration.datasync.errorcodes;

/**
 * Error codes
 * 
 * @author M1046129 - Jagadishwari
 *
 */
public enum ErrorCodes {

	PRG_DATA_SYNC_001, //Records Not Found For Requested Date Range
	PRG_DATA_SYNC_002, //Records Not Found For Requested Registration Center
	PRG_DATA_SYNC_003, //Invalid User Id
	PRG_DATA_SYNC_004, //Records Not Found For Requested PreRegId
	PRG_DATA_SYNC_005, //Failed to create a zip file
//	PRG_DATA_SYNC_006; //Document Not Found
	PRG_REVESE_DATA_SYNC_001, //Failed to store Pre-Reg Ids
	PRG_PAM_APP_002,   //Registration table not accessible
	PRG_DATA_SYNC_007, // DEMOGRAPHIC_GET_STATUS_FAILED
	PRG_DATA_SYNC_008, //DOCUMENT_GET_STATUS_FAILED
	PRG_DATA_SYNC_009, //INVALID_REGISTRATION_CENTER_ID
	PRG_DATA_SYNC_010;//INVALID_REQUESTED_DATE
}
