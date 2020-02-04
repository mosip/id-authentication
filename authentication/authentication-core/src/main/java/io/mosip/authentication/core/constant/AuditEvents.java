package io.mosip.authentication.core.constant;

/**
 * The Enum AuditEvents - Contains all the events for auditing.
 *
 * @author Manoj SP
 */
public enum AuditEvents {
	
	/** The auth request response. */
	AUTH_REQUEST_RESPONSE("IDA-001", "BUSINESS", "Authentication Request"),
	
	/** The otp trigger request response. */
	OTP_TRIGGER_REQUEST_RESPONSE("IDA-002", "BUSINESS", "OTP Request"),
	
	/** The ekyc request response. */
	EKYC_REQUEST_RESPONSE("IDA-003", "BUSINESS", "eKYC Request"),
	
	/** The internal request response. */
	INTERNAL_REQUEST_RESPONSE("IDA-004", "SYSTEM", "Internal Authentication Request"),
	
	/** The internal otp trigger request response. */
	INTERNAL_OTP_TRIGGER_REQUEST_RESPONSE("IDA-005", "SYSTEM", "Internal OTP Request"),
	
	/** The retrieve auth type status request response. */
	RETRIEVE_AUTH_TYPE_STATUS_REQUEST_RESPONSE("IDA-006", "SYSTEM", "Retrieve Auth Type Status Request"),
	
	/** The update auth type status request response. */
	UPDATE_AUTH_TYPE_STATUS_REQUEST_RESPONSE("IDA-007", "SYSTEM", "Update Auth Type Status Request"),
	
	/** The retrieve auth transaction history request response. */
	RETRIEVE_AUTH_TRANSACTION_HISTORY_REQUEST_RESPONSE("IDA-008", "SYSTEM", "Retrieve Auth Transaction  History Request"),
	
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
