package io.mosip.authentication.core.constant;

/**
 * @author Manoj SP
 *
 */
public enum AuditEvents {
	AUTH_REQUEST_RESPONSE("IDA-EVT-001", "Business Event"),
	OTP_TRIGGER_REQUEST_RESPONSE("IDA-EVT-002", "Business Event"),
	INTERNAL_REQUEST_RESPONSE("IDA-EVT-003", "System Event");
	
	private final String eventId;
	private final String eventType;

	private AuditEvents(String eventId, String eventType) {
		this.eventId = eventId;
		this.eventType = eventType;
	}

	public String getEventId() {
		return eventId;
	}

	public String getEventType() {
		return eventType;
	}
	
	public String getEventName() {
		return this.name();
	}
	
}
