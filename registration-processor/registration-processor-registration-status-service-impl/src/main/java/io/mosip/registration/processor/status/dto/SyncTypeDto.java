package io.mosip.registration.processor.status.dto;


/**	
 * The Enum SyncTypeDto.
 *
 * @author Girish Yarru
 */
public enum SyncTypeDto {

	/** The new registration. */
	NEW("NEW"),

    /** The update uin. */
	UPDATE("UPDATE"),

	/** The lost uin. */
	LOST("LOST"),


	/** The activate uin. */
	ACTIVATED("ACTIVATED"),

	/** The deactivate uin. */
	DEACTIVATED("DEACTIVATED"),
	
	/** The res update. */
	RES_UPDATE("RES_UPDATE");

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
	 * @return the value
	 */
	public String getValue() {
		return this.value;
	}

}