package io.mosip.preregistration.core.code;

/**
 * This enum is used to define the constants for audit logging.
 * 
 * @author Jagadishwari S
 * @since 1.0.0
 */
public enum AuditLogVariables {
	/** The application id. */
	MOSIP_1,

	/** The application name. */
	PREREGISTRATION,

	/** No id provided *. */
	NO_ID,

	/** The list of registration id's. */
	MULTIPLE_ID,

	/** The reference id type. */
	PRE_REGISTRATION_ID,

	/** The created by and session userId. */
	SYSTEM,

	DEMOGRAPHY_SERVICE,

	DEM,

	DOCUMENT_SERVICE,

	DOC,

	DATASYNC_SERVICE,

	DAT,
	BAT,

	REVERSE_DATASYNC_SERVICE,

	REV,

	BOOKING_SERVICE, 
	BOOK,
	NOTIFICATION_SERVICE,
	
	NOTIFY,
	
	AUTHENTICATION_SERVICE,
	
	AUTHENTICATION,
	
	CONSUMED_BATCH_SERVICE,
	
	EXPIRED_BATCH_SERVICE
}
