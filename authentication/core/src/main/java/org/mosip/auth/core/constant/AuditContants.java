package org.mosip.auth.core.constant;

/**
 * The Enum AuditContants.
 *
 * @author Manoj SP
 */
public enum AuditContants {
	
	/** The module id. */
	MODULE_ID("module_id"),
	
	/** The module name. */
	MODULE_NAME("module_name"),
	
	/** The module description. */
	MODULE_DESCRIPTION("description");
	
	/** The message. */
	private final String message;
	
	/**
	 * Instantiates a new audit contants.
	 *
	 * @param message the message
	 */
	private AuditContants(String message) {
		this.message = message;
	}
	
	/**
	 * Gets the message.
	 *
	 * @param key the key
	 * @return the message
	 */
	public String getMessage(String key) {
		return message;
	}

}
