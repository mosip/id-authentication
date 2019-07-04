package io.mosip.idrepository.core.constant;

/**
 * The Enum AuditEvents - Contains all the events for auditing.
 *
 * @author Manoj SP
 */
public enum AuditEvents {

	CREATE_IDENTITY_REQUEST_RESPONSE("IDR-001", "System Event"),

	UPDATE_IDENTITY_REQUEST_RESPONSE("IDR-002", "System Event"),

	RETRIEVE_IDENTITY_REQUEST_RESPONSE_UIN("IDR-003", "System Event"),
	
	RETRIEVE_IDENTITY_REQUEST_RESPONSE_RID("IDR-004", "System Event"),
	
	CREATE_VID("IDR-005", "System Event"),
	
	RETRIEVE_VID_UIN("IDR-006", "System Event"),
	
	REVOKE_VID("IDR-007","System Event"),
	
	REGENERATE_VID("IDR-008", "System Event"),
	
	UPDATE_VID_STATUS("IDR-009", "System Event");
	
	

	/** The event id. */
	private final String eventId;

	/** The event type. */
	private final String eventType;

	/**
	 * Instantiates a new audit events.
	 *
	 * @param eventId   the event id
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
	