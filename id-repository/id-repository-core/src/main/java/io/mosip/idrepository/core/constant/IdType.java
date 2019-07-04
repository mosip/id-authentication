package io.mosip.idrepository.core.constant;

/**
 * The Enum IdType - Contains all Id types used in Id Repository.
 *
 * @author Prem Kumar.
 */
public enum IdType {

	/** The reg id. */
	REG_ID("RegistrationId"),
	
	/** The vid. */
	VID("VID"),
	
	/** The uin. */
	UIN("UIN");
	
	/** The id Type. */
	private final String idType;

	/**
	 * Gets the id type.
	 *
	 * @return the id type
	 */
	public String getIdType() {
		return idType;
	}
	
	/**
	 * Instantiates a new audit contants.
	 *
	 * @param idType the idType
	 */
	private IdType(String idType) {
		this.idType = idType;
	}
}
