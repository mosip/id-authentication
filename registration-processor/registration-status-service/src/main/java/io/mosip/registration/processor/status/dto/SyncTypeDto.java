package io.mosip.registration.processor.status.dto;

/**
 * The Enum SyncTypeDto.
 *
 * @author Girish Yarru
 */
public enum SyncTypeDto {

	/** The new registration. */
	NEW("NEW"),
		
	/** The correction. */
	CORRECTION("CORRECTION"),
	
	/** The update uin. */
	UPDATE("UPDATE"),
	
	/** The lost uin. */
	LOST_UIN("LOST UIN"),
	
	/** The update uin. */
	UPDATE_UIN("UPDATE UIN"),
	
	/** The activate uin. */
	ACTIVATE_UIN("ACTIVATE UIN"),
	
	/** The deactivate uin. */
	DEACTIVATE_UIN("DEACTIVATE UIN");
	
	/** The value. */
	private String value;
	
	/**
	 * Instantiates a new sync type dto.
	 *
	 * @param value the value
	 */
	private SyncTypeDto (String value) {
		this.value = value;
	}
	
	/**
	 * Gets the value.
	 *
	 * @param value the value
	 * @return the value
	 */
	public String getValue() {
		return this.value;
	}

}