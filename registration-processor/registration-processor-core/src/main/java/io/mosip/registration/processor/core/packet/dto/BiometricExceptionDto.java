package io.mosip.registration.processor.core.packet.dto;

/**
 * 	
 * This contains the attributes of BiometricException object to be displayed in
 * PacketMetaInfo JSON.
 *
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public class BiometricExceptionDto {

	/** The language. */
	private String language;
	
	/** The type. */
	private String type;
	
	/** The missing biometric. */
	private String missingBiometric;
	
	/** The exception description. */
	private String exceptionDescription;
	
	/** The exception type. */
	private String exceptionType;

	/**
	 * Gets the language.
	 *
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * Sets the language.
	 *
	 * @param language            the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the type.
	 *
	 * @param type            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Gets the missing biometric.
	 *
	 * @return the missingBiometric
	 */
	public String getMissingBiometric() {
		return missingBiometric;
	}

	/**
	 * Sets the missing biometric.
	 *
	 * @param missingBiometric            the missingBiometric to set
	 */
	public void setMissingBiometric(String missingBiometric) {
		this.missingBiometric = missingBiometric;
	}

	/**
	 * Gets the exception description.
	 *
	 * @return the exceptionDescription
	 */
	public String getExceptionDescription() {
		return exceptionDescription;
	}

	/**
	 * Sets the exception description.
	 *
	 * @param exceptionDescription            the exceptionDescription to set
	 */
	public void setExceptionDescription(String exceptionDescription) {
		this.exceptionDescription = exceptionDescription;
	}

	/**
	 * Gets the exception type.
	 *
	 * @return the exceptionType
	 */
	public String getExceptionType() {
		return exceptionType;
	}

	/**
	 * Sets the exception type.
	 *
	 * @param exceptionType            the exceptionType to set
	 */
	public void setExceptionType(String exceptionType) {
		this.exceptionType = exceptionType;
	}

}
