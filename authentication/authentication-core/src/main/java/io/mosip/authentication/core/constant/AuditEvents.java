package io.mosip.authentication.core.constant;

/**
 * The Enum AuditEvents - Contains all the events for auditing.
 *
 * @author Manoj SP
 */
public enum AuditEvents {
	
	/** The auth request response. */
	AUTH_REQUEST_RESPONSE("IDA-EVT-001", "Business Event"),
	
	/** The otp trigger request response. */
	OTP_TRIGGER_REQUEST_RESPONSE("IDA-EVT-002", "Business Event"),
	
	/** The internal request response. */
	INTERNAL_REQUEST_RESPONSE("IDA-EVT-003", "System Event"),
	
	/** Static_Pin_Storage_Request_Response */
	STATIC_PIN_STORAGE_REQUEST_RESPONSE("IDA-EVT-004","Business Event");
	
	/** The event id. */
	private final String eventId;
	
	/** The event type. */
	private final String eventType;

	/**
	 * Instantiates a new audit events.
	 *
	 * @param eventId the event id
	 * @param eventType the event type
	 */
	private AuditEvents(String eventId, String eventType) {
		this.eventId = eventId;
		this.eventType = eventType;
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
		return this.name();
	}
	
}
