package io.mosip.registration.processor.core.packet.dto;

/**
 * This contains the attributes of BiometricException object to be displayed in
 * PacketMetaInfo JSON
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public class BiometricException {

	private String language;
	private String type;
	private String missingBiometric;
	private String exceptionDescription;
	private String exceptionType;

	/**
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * @param language
	 *            the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the missingBiometric
	 */
	public String getMissingBiometric() {
		return missingBiometric;
	}

	/**
	 * @param missingBiometric
	 *            the missingBiometric to set
	 */
	public void setMissingBiometric(String missingBiometric) {
		this.missingBiometric = missingBiometric;
	}

	/**
	 * @return the exceptionDescription
	 */
	public String getExceptionDescription() {
		return exceptionDescription;
	}

	/**
	 * @param exceptionDescription
	 *            the exceptionDescription to set
	 */
	public void setExceptionDescription(String exceptionDescription) {
		this.exceptionDescription = exceptionDescription;
	}

	/**
	 * @return the exceptionType
	 */
	public String getExceptionType() {
		return exceptionType;
	}

	/**
	 * @param exceptionType
	 *            the exceptionType to set
	 */
	public void setExceptionType(String exceptionType) {
		this.exceptionType = exceptionType;
	}

}
