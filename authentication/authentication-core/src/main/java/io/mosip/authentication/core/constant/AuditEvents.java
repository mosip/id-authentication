package io.mosip.authentication.core.constant;

/**
 * The Enum AuditEvents - Contains all the events for auditing.
 *
 * @author Manoj SP
 */
public enum AuditEvents {
	
	/** The auth request response. */
	AUTH_REQUEST_RESPONSE("IDA_001", "User", "Authentication Request"),
	
	/** The otp trigger request response. */
	OTP_TRIGGER_REQUEST_RESPONSE("IDA_002", "User", "OTP Request"),
	
	/** The ekyc request response. */
	EKYC_REQUEST_RESPONSE("IDA_003", "User", "eKYC Request"),
		
	/** The internal request response. */
	INTERNAL_REQUEST_RESPONSE("IDA_004", "System", "Internal Authentication Request"),
	
	/** The internal otp trigger request response. */
	INTERNAL_OTP_TRIGGER_REQUEST_RESPONSE("IDA_005", "System", "Internal OTP Request"),
	
	/** The retrieve auth type status request response. */
	RETRIEVE_AUTH_TYPE_STATUS_REQUEST_RESPONSE("IDA_006", "System", "Retrieve Auth Type Status Request"),
	
	/** The update auth type status request response. */
	UPDATE_AUTH_TYPE_STATUS_REQUEST_RESPONSE("IDA_007", "System", "Update Auth Type Status Request"),
	
	/** The retrieve auth transaction history request response. */
	RETRIEVE_AUTH_TRANSACTION_HISTORY_REQUEST_RESPONSE("IDA_008", "System", "Retrieve Auth Transaction  History Request"),
	
	CREDENTIAL_ISSUED_EVENT("IDA_009", "System", "Credential Issued"),
	
	REMOVE_ID_EVENT("IDA_010", "System", "Remove ID"),
	
	DEACTIVATE_ID_EVENT("IDA_011", "System", "Deactivate ID"),
	
	ACTIVATE_ID_EVENT("IDA_012", "System", "Activate ID"),
	
	CREDENTIAL_STORED_EVENT("IDA_013", "System", "Credential Issued"),

	KYC_REQUEST_RESPONSE("IDA_014", "System", "Kyc Auth Request"),

	KYC_EXCHANGE_REQUEST_RESPONSE("IDA_015", "System", "Kyc Exchange Request"),

	KEY_BINDIN_REQUEST_RESPONSE("IDA_016", "System", "Identity Key Binding Request"),

	VCI_EXCHANGE_REQUEST_RESPONSE("IDA_017", "System", "Vci Exchange Request"),
	
	/**  Static_Pin_Storage_Request_Response. */
	STATIC_PIN_STORAGE_REQUEST_RESPONSE("IDA-EVT-OLD-006","BUSINESS", ""),//not applicable for release v1
	
	
	/** The vid generate request response. */
	VID_GENERATE_REQUEST_RESPONSE("IDA-EVT-OLD-007","BUSINESS", "");//not applicable for release v1
	
	/** The event id. */
	private final String eventId;
	
	/** The event type. */
	private final String eventType;
	
	/** The event name. */
	private final String eventName;

	/**
	 * Instantiates a new audit events.
	 *
	 * @param eventId the event id
	 * @param eventType the event type
	 * @param eventName the event name
	 */
	private AuditEvents(String eventId, String eventType, String eventName) {
		this.eventId = eventId;
		this.eventType = eventType;
		this.eventName=eventName;
	}

	/**
	 * Gets the event id.
	 *
	 * @return the event id
	 */
	public String getEventId() {
		return eventId;
	}

	/**
	 * Gets the event type.
	 *
	 * @return the event type
	 */
	public String getEventType() {
		return eventType;
	}
	
	/**
	 * Gets the event name.
	 *
	 * @return the event name
	 */
	public String getEventName() {
		return this.eventName;
	}
	
}
