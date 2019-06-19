package io.mosip.idrepository.core.constant;
/**
 * 
 * @author Prem Kumar.
 *
 */
public enum IdType {

	REG_ID("RegistrationId"),
	VID("VID"),
	UIN("UIN");
	
	/** The id Type. */
	private final String idType;

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
