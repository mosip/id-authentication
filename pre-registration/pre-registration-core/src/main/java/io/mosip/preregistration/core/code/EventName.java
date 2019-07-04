package io.mosip.preregistration.core.code;

/**
 * The Enum EventName.
 * 
 * @author Jagadishwari S
 * @since 1.0.0
 */
public enum EventName {

	/** The get. */
	RETRIEVE,

	/** The update. */
	UPDATE,

	/** The document copy. */
	COPY,

	/** The delete. */
	DELETE,

	/** The document upload.. */
	UPLOAD,

	/** The exception. */
	EXCEPTION,

	/** The authentication. */
	AUTHENTICATION,

	/** The save. */
	PERSIST,

	/** The Data sync. */
	SYNC,

	/** The Reverse Data sync. */
	REVERSESYNC,
	
	/** Triggering notification . */
	NOTIFICATION,
	
	/** Triggering batch-service for consumed status . */
	CONSUMEDSTATUS,
	
	/** Triggering batch-service for expired status . */
	EXPIREDSTATUS

}
