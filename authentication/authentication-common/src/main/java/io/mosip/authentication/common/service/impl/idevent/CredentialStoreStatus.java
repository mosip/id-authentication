package io.mosip.authentication.common.service.impl.idevent;

/**
 * The Enum CredentialStoreStatus.
 *
 * @author Loganathan Sekar
 */
public enum CredentialStoreStatus {

	/** The new status. */
	NEW,
	/** The stored status. */
	STORED,
	/** The failed status. */
	FAILED,
	/** The failed with max retries status. */
	FAILED_WITH_MAX_RETRIES,
	/** The failed non recoverable. */
	FAILED_NON_RECOVERABLE

}
