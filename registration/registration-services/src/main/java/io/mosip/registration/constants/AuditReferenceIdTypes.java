package io.mosip.registration.constants;

/**
 * Enum for References Id Types to be used in Audit
 *
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public enum AuditReferenceIdTypes {

	USER_ID("USER_ID"),
	REGISTRATION_ID("REGISTRATION_ID"),
	APPLICATION_ID("APPLICATION_ID");

	/**
	 * The constructor
	 */
	private AuditReferenceIdTypes(String referenceTypeId) {
		this.referenceTypeId = referenceTypeId;
	}

	private final String referenceTypeId;

	/**
	 * @return the referenceTypeId
	 */
	public String getReferenceTypeId() {
		return referenceTypeId;
	}

}
