package io.mosip.registration.processor.core.exception.util;

/**
 *
 * @author M1048399 Horteppa
 * 
 * The Enum PlatformSuccessMessages.
 */
public enum PlatformSuccessMessages {
	
	/** The rpr pkr packet validate. */
	RPR_PKR_PACKET_VALIDATE(PlatformErrorConstants.RPR_PACKET_VALIDATOR_MODULE + "000",
			"Packet Validation Success"),
	
	
	/** The rpr pkr osi validate. */
	RPR_PKR_OSI_VALIDATE(PlatformErrorConstants.RPR_OSI_VALIDATOR_MODULE+"000","OSI Validation Success"),
	
	/** The rpr pkr osi validate. */
	RPR_PKR_DEMO_DE_DUP(PlatformErrorConstants.RPR_DEMO_DEDUPE_MODULE+"000","Demo-de-dupe Success"),
	
	RPR_PKR_DEMO_DE_DUP_POTENTIAL_DUPLICATION_FOUND(PlatformErrorConstants.RPR_DEMO_DEDUPE_MODULE+"000","Potential duplicate packet found for registration id : ");
	
	/** The success message. */
	private final String successMessage;

	/** The success code. */
	private final String successCode;

	/**
	 * Instantiates a new platform success messages.
	 *
	 * @param errorCode the error code
	 * @param errorMsg the error msg
	 */
	private PlatformSuccessMessages(String errorCode, String errorMsg) {
		this.successCode = errorCode;
		this.successMessage = errorMsg;
	}

	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	public String getMessage() {
		return this.successMessage;
	}

	/**
	 * Gets the code.
	 *
	 * @return the code
	 */
	public String getCode() {
		return this.successCode;
	}

}
